package com.example.desporto24.storage;

import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

public interface MediaStorage {
    URI store(String key, MultipartFile file);
    URI storeVideo(String key, MultipartFile file);
    URI storeCommunityPhoto(String key, MultipartFile file);
    void delete(String key);

    /** Reads a stored public object (used to move media to protected storage). */
    default java.io.InputStream open(String key) {
        throw new UnsupportedOperationException("Reading media is not supported");
    }

    /** Writes already validated bytes under an exact server-generated key. */
    default URI putValidated(String objectKey, byte[] data, String mimeType) {
        throw new UnsupportedOperationException("Writing media is not supported");
    }
}
