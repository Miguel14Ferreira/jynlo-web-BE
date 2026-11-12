package com.example.desporto24.model.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Extra private attachment. Object keys must never be serialized to clients. */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChatAttachment {
    @JsonIgnore
    @Column(name = "storage_key", nullable = false, length = 240)
    private String storageKey;
    @Column(name = "file_name", nullable = false, length = 160)
    private String filename;
    @Column(name = "mime_type", nullable = false, length = 60)
    private String mimeType;
    @Column(name = "file_size", nullable = false)
    private Long size;
}
