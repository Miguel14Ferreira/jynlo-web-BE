package com.example.desporto24.service;

import com.example.desporto24.model.chat.PrivateChatAttachment;
import com.example.desporto24.storage.PrivateChatFileStorage;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/** One atomic message can carry up to four individually authorized private files. */
public final class PrivateChatAttachmentBatch {
    private PrivateChatAttachmentBatch() {}
    public static final int MAX_FILES = 4;
    public static final long MAX_TOTAL_BYTES = 40L * 1024 * 1024;

    public static void validate(List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.size() > MAX_FILES
                || files.stream().anyMatch(f -> f == null || f.isEmpty())
                || files.stream().mapToLong(MultipartFile::getSize).sum() > MAX_TOTAL_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose 1–4 files up to 40 MB total");
        }
    }

    public static StoredBatch store(PrivateChatFileStorage storage, List<MultipartFile> files) {
        validate(files);
        List<PrivateChatFileStorage.StoredFile> stored = new ArrayList<>();
        try {
            for (MultipartFile file : files) stored.add(storage.store(file));
            return new StoredBatch(stored);
        } catch (RuntimeException failure) {
            cleanup(storage, stored, failure);
            throw failure;
        }
    }

    public static void cleanup(PrivateChatFileStorage storage,
                               List<PrivateChatFileStorage.StoredFile> stored, RuntimeException failure) {
        for (var item : stored) {
            try { storage.delete(item.key()); }
            catch (RuntimeException cleanup) { failure.addSuppressed(cleanup); }
        }
    }

    public record StoredBatch(List<PrivateChatFileStorage.StoredFile> files) {
        public PrivateChatFileStorage.StoredFile first() { return files.get(0); }
        public List<PrivateChatAttachment> extras() {
            return files.subList(1, files.size()).stream()
                    .map(f -> new PrivateChatAttachment(f.key(), f.name(), f.mime(), f.size()))
                    .toList();
        }
    }

    public record PublicMetadata(int index, String filename, String mimeType, Long size) {}
    public static List<PublicMetadata> metadata(String name, String mime, Long size,
                                                  List<PrivateChatAttachment> extras) {
        if (name == null) return List.of();
        List<PublicMetadata> items = new ArrayList<>();
        items.add(new PublicMetadata(0, name, mime, size));
        if (extras != null) for (int i = 0; i < extras.size(); i++) {
            var item = extras.get(i);
            items.add(new PublicMetadata(i + 1, item.getFilename(), item.getMimeType(), item.getSize()));
        }
        return items;
    }
}
