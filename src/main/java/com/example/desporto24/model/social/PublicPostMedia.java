package com.example.desporto24.model.social;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Public post media. Storage keys must never appear in API responses. */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublicPostMedia {
    @Column(name = "media_url", length = 1200, nullable = false)
    private String url;
    @Column(name = "media_storage_key", length = 800, nullable = false)
    private String storageKey;
    @Column(name = "media_mime_type", length = 40, nullable = false)
    private String mimeType;
}
