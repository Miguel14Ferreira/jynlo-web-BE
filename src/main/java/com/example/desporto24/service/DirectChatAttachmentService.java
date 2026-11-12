package com.example.desporto24.service;

import com.example.desporto24.Chat.ChatRepository;
import com.example.desporto24.model.Chat;
import com.example.desporto24.service.interfaces.ChatService;
import com.example.desporto24.storage.PrivateChatFileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Attachment upload is a normal, authorized direct message and therefore
 * produces the same persisted event/notification/read receipt as text.
 * Never pass storage keys to callers or serve a raw public storage URI.
 */
@Service
@RequiredArgsConstructor
public class DirectChatAttachmentService {
    private final ChatService chats;
    private final ChatRepository messages;
    private final PrivateChatFileStorage files;

    public Chat send(String actor, String peer, MultipartFile file,
                     String caption, Long replyToMessageId) {
        return sendBatch(actor, peer, java.util.List.of(file), caption, replyToMessageId);
    }

    public Chat sendBatch(String actor, String peer, java.util.List<MultipartFile> uploads,
                          String caption, Long replyToMessageId) {
        var friendship = chats.getChatInfo(actor, peer);
        if (friendship.isChatBlocked()) throw new AccessDeniedException("Chat is blocked");
        String text = caption == null ? "" : caption.trim();
        if (text.length() > 2000 || replyToMessageId != null && replyToMessageId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid attachment message");
        }
        var batch = PrivateChatAttachmentBatch.store(files, uploads);
        var first = batch.first();
        Chat msg = new Chat();
        msg.setUsername1(actor);
        msg.setUsername2(peer);
        msg.setText(text.isEmpty() ? "📎 " + first.name() : text);
        msg.setReplyToMessageId(replyToMessageId);
        msg.setAttachmentKey(first.key());
        msg.setAttachmentName(first.name());
        msg.setAttachmentMimeType(first.mime());
        msg.setAttachmentSize(first.size());
        msg.setExtraAttachments(new java.util.ArrayList<>(batch.extras()));
        try {
            return chats.sendMessage(msg);
        } catch (RuntimeException failure) {
            PrivateChatAttachmentBatch.cleanup(files, batch.files(), failure);
            throw failure;
        }
    }

    public boolean available(String actor, String peer) {
        var friendship = chats.getChatInfo(actor, peer);
        return !friendship.isChatBlocked() && files.isConfigured();
    }

    public AttachmentContent download(String actor, String peer, Long messageId) {
        if (messageId == null || messageId <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }
        // Friendship/block checks are identical to fetching text history.
        chats.getChatInfo(actor, peer);
        Chat msg = messages.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
        boolean direct = (msg.getUsername1().equalsIgnoreCase(actor)
                && msg.getUsername2().equalsIgnoreCase(peer))
                || (msg.getUsername1().equalsIgnoreCase(peer)
                && msg.getUsername2().equalsIgnoreCase(actor));
        if (!direct || msg.getAttachmentKey() == null || msg.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }
        return new AttachmentContent(files.read(msg.getAttachmentKey()),
                msg.getAttachmentMimeType(), msg.getAttachmentName());
    }

    public AttachmentContent downloadAt(String actor, String peer, Long messageId, int index) {
        if (index == 0) return download(actor, peer, messageId);
        if (index < 1 || index >= PrivateChatAttachmentBatch.MAX_FILES) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }
        chats.getChatInfo(actor, peer);
        Chat msg = messages.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
        boolean direct = (msg.getUsername1().equalsIgnoreCase(actor)
                && msg.getUsername2().equalsIgnoreCase(peer))
                || (msg.getUsername1().equalsIgnoreCase(peer)
                && msg.getUsername2().equalsIgnoreCase(actor));
        if (!direct || msg.getDeletedAt() != null || msg.getAttachmentKey() == null
                || msg.getExtraAttachments() == null || index > msg.getExtraAttachments().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }
        var extra = msg.getExtraAttachments().get(index - 1);
        return new AttachmentContent(files.read(extra.getStorageKey()), extra.getMimeType(), extra.getFilename());
    }

    public record AttachmentContent(byte[] content, String mimeType, String filename) {}
}
