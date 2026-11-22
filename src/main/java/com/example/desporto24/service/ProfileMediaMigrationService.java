package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.PublicPostMedia;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialStory;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialStoryRepository;
import com.example.desporto24.storage.MediaStorage;
import com.example.desporto24.storage.PrivateProfileMediaStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Moves one person's personal post and Story media between public storage and
 * the protected bucket when they change profile visibility.
 *
 * Order matters and makes the move safe to retry:
 *  1. copy every file and verify the copy byte-for-byte (SHA-256);
 *  2. switch all database references in ONE transaction, which also queues the
 *     old objects in the durable deletion outbox;
 *  3. the outbox deletes the old objects afterwards.
 * If anything fails before step 2, the database is untouched, the new copies
 * are removed (best effort) and the visibility change is refused.
 * Community and team Stories have their own rules and are never moved.
 */
@Slf4j
@Service
public class ProfileMediaMigrationService {
    static final long MAX_BYTES = 60L * 1024 * 1024;

    private final SocialPostRepository posts;
    private final SocialStoryRepository stories;
    private final MediaStorage publicStorage;
    private final PrivateProfileMediaStorage privateStorage;
    private final ProfileMediaDeletionOutbox outbox;
    private final TransactionTemplate transactions;

    public ProfileMediaMigrationService(SocialPostRepository posts, SocialStoryRepository stories,
                                        MediaStorage publicStorage, PrivateProfileMediaStorage privateStorage,
                                        ProfileMediaDeletionOutbox outbox,
                                        org.springframework.transaction.PlatformTransactionManager transactionManager) {
        this.posts = posts;
        this.stories = stories;
        this.publicStorage = publicStorage;
        this.privateStorage = privateStorage;
        this.outbox = outbox;
        // The database switch commits on its own, never inside a slow outer request transaction.
        this.transactions = new TransactionTemplate(transactionManager);
        this.transactions.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** One media reference to move: where it is now and how to write the result back. */
    interface Slot {
        String key();
        String url();
        String type();          // "posts" | "stories"
        String protectedRoute();
        void apply(String newKey, String newUrl);
    }

    public record Result(int moved) {}

    public Result makePrivate(Profile owner) {
        return move(owner, true);
    }

    public Result makePublic(Profile owner) {
        return move(owner, false);
    }

    private Result move(Profile owner, boolean toPrivate) {
        if (!privateStorage.isConfigured())
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Armazenamento protegido indisponível");
        List<SocialPost> ownPosts = new ArrayList<>();
        List<SocialStory> ownStories = new ArrayList<>();
        List<Slot> pending = new ArrayList<>();
        for (SocialPost post : posts.findByAuthorId(owner.getId())) {
            if (post.getActivityType() != null) continue;
            int before = pending.size();
            collectPost(post, owner.getId(), toPrivate, pending);
            if (pending.size() > before) ownPosts.add(post);
        }
        for (SocialStory story : stories.findByAuthorId(owner.getId())) {
            if (story.getCommunity() != null || story.getTeam() != null) continue;
            Slot slot = storySlot(story);
            if (needsMove(slot, owner.getId(), toPrivate)) {
                pending.add(slot);
                ownStories.add(story);
            }
        }
        if (pending.isEmpty()) return new Result(0);

        List<String> created = new ArrayList<>();
        List<String> oldKeys = new ArrayList<>();
        List<Runnable> updates = new ArrayList<>();
        try {
            for (Slot slot : pending) {
                String source = slot.key();
                if (source == null || source.isBlank())
                    throw new IllegalStateException("Media reference without a storage key");
                String extension = extension(source);
                String mime = mime(extension);
                byte[] bytes = read(toPrivate ? publicStorage.open(source) : privateStorage.open(source));
                String digest = sha256(bytes);
                final String newKey;
                final String newUrl;
                if (toPrivate) {
                    newKey = "private-profile-media/" + slot.type() + "/" + owner.getId() + "/" + UUID.randomUUID() + extension;
                    privateStorage.put(newKey, bytes, mime);
                    created.add(newKey);
                    if (!digest.equals(sha256(read(privateStorage.open(newKey)))))
                        throw new IllegalStateException("Protected copy does not match the original");
                    newUrl = slot.protectedRoute();
                } else {
                    newKey = slot.type() + "/" + owner.getId() + "/" + UUID.randomUUID() + extension;
                    URI uri = publicStorage.putValidated(newKey, bytes, mime);
                    created.add(newKey);
                    if (!digest.equals(sha256(read(publicStorage.open(newKey)))))
                        throw new IllegalStateException("Public copy does not match the original");
                    newUrl = uri.toString();
                }
                oldKeys.add(source);
                updates.add(() -> slot.apply(newKey, newUrl));
            }
        } catch (RuntimeException failure) {
            discard(created);
            log.warn("profile_media_move_failed owner={} toPrivate={} copied={} of {}",
                    owner.getId(), toPrivate, created.size(), pending.size(), failure);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível preparar as tuas fotografias e vídeos. Nada foi alterado; tenta novamente.");
        }

        try {
            transactions.executeWithoutResult(status -> {
                updates.forEach(Runnable::run);
                posts.saveAll(ownPosts);
                stories.saveAll(ownStories);
                outbox.enqueueAll(oldKeys);
            });
        } catch (RuntimeException failure) {
            discard(created);
            throw failure;
        }
        log.info("profile_media_moved owner={} toPrivate={} items={}", owner.getId(), toPrivate, pending.size());
        return new Result(pending.size());
    }

    private void collectPost(SocialPost post, Long ownerId, boolean toPrivate, List<Slot> out) {
        String base = "/api/profile-media/posts/" + post.getId() + "/";
        Slot image = new Slot() {
            public String key() { return post.getImageStorageKey(); }
            public String url() { return post.getImageUrl(); }
            public String type() { return "posts"; }
            public String protectedRoute() { return base + "image"; }
            public void apply(String k, String u) { post.setImageStorageKey(k); post.setImageUrl(u); }
        };
        Slot video = new Slot() {
            public String key() { return post.getVideoStorageKey(); }
            public String url() { return post.getVideoUrl(); }
            public String type() { return "posts"; }
            public String protectedRoute() { return base + "video"; }
            public void apply(String k, String u) { post.setVideoStorageKey(k); post.setVideoUrl(u); }
        };
        if (needsMove(image, ownerId, toPrivate)) out.add(image);
        if (needsMove(video, ownerId, toPrivate)) out.add(video);
        List<PublicPostMedia> extra = post.getExtraMedia();
        if (extra == null) return;
        for (int i = 0; i < extra.size(); i++) {
            final int index = i;
            PublicPostMedia asset = extra.get(i);
            if (asset == null) throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Há uma publicação antiga com um álbum incompleto. Contacta o suporte.");
            Slot slot = new Slot() {
                public String key() { return extra.get(index).getStorageKey(); }
                public String url() { return extra.get(index).getUrl(); }
                public String type() { return "posts"; }
                public String protectedRoute() { return base + "extra-" + index; }
                public void apply(String k, String u) {
                    extra.set(index, new PublicPostMedia(u, k, extra.get(index).getMimeType()));
                }
            };
            if (needsMove(slot, ownerId, toPrivate)) out.add(slot);
        }
    }

    private Slot storySlot(SocialStory story) {
        return new Slot() {
            public String key() { return story.getMediaStorageKey(); }
            public String url() { return story.getMediaUrl(); }
            public String type() { return "stories"; }
            public String protectedRoute() { return "/api/profile-media/stories/" + story.getId(); }
            public void apply(String k, String u) { story.setMediaStorageKey(k); story.setMediaUrl(u); }
        };
    }

    /** Same rule as the readiness audit: protected = private key of this owner AND the authenticated route. */
    static boolean isProtected(Slot slot, Long ownerId) {
        String key = slot.key();
        return PrivateProfileMediaStorage.isPrivateKey(key)
                && key.startsWith("private-profile-media/" + slot.type() + "/" + ownerId + "/")
                && slot.protectedRoute().equals(slot.url());
    }

    private static boolean needsMove(Slot slot, Long ownerId, boolean toPrivate) {
        boolean empty = (slot.key() == null || slot.key().isBlank()) && (slot.url() == null || slot.url().isBlank());
        if (empty) return false;
        return toPrivate != isProtected(slot, ownerId);
    }

    private void discard(List<String> keys) {
        for (String key : keys) {
            try {
                if (PrivateProfileMediaStorage.isPrivateKey(key)) privateStorage.delete(key);
                else publicStorage.delete(key);
            } catch (RuntimeException ignored) {
                // An orphaned copy is harmless; the database never points to it.
            }
        }
    }

    static String extension(String key) {
        int dot = key.lastIndexOf('.');
        String ext = dot < 0 ? "" : key.substring(dot).toLowerCase(Locale.ROOT);
        if (!ext.matches("\\.(?:jpg|jpeg|png|webp|mp4|webm)"))
            throw new IllegalStateException("Unsupported media type");
        return ext;
    }

    static String mime(String extension) {
        return switch (extension) {
            case ".png" -> "image/png";
            case ".webp" -> "image/webp";
            case ".mp4" -> "video/mp4";
            case ".webm" -> "video/webm";
            default -> "image/jpeg";
        };
    }

    private static byte[] read(InputStream input) {
        try (InputStream in = input) {
            byte[] data = in.readNBytes((int) MAX_BYTES + 1);
            if (data.length == 0 || data.length > MAX_BYTES) throw new IllegalStateException("Invalid media size");
            return data;
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not read media", e);
        }
    }

    private static String sha256(byte[] data) {
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
