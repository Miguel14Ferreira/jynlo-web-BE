package com.example.desporto24.storage;

import com.example.desporto24.configuration.MediaStorageProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class PrivateProfileMediaStorageTest {
    @Test void onlyServerGeneratedPrivatePostAndStoryKeysAreAllowed() {
        assertTrue(PrivateProfileMediaStorage.isPrivateKey("private-profile-media/posts/42/some-id.webp"));
        assertTrue(PrivateProfileMediaStorage.isPrivateKey("private-profile-media/stories/42/some-id.mp4"));
        assertFalse(PrivateProfileMediaStorage.isPrivateKey("posts/42/some-id.webp"));
        assertFalse(PrivateProfileMediaStorage.isPrivateKey("private-profile-media/posts/42/../secret.jpg"));
        assertFalse(PrivateProfileMediaStorage.isPrivateKey("private-profile-media/posts/42/file.svg"));
        assertFalse(PrivateProfileMediaStorage.isPrivateKey("private-profile-media/posts/42/%2e%2e.jpg"));
    }
    @Test void cannotConfigurePrivateStorageInsidePublicMediaDirectory() {
        MediaStorageProperties props = new MediaStorageProperties();
        props.setProvider("local");
        props.setLocalRoot("/tmp/jynlo/public");
        props.setPrivateRoot("/tmp/jynlo/public/private");
        var storage = new PrivateProfileMediaStorage(props);
        assertFalse(storage.isConfigured());
        assertThrows(ResponseStatusException.class,
                ()->storage.open("private-profile-media/posts/42/some-id.jpg"));
        props.setPrivateRoot("/tmp/jynlo/private");
        assertTrue(storage.isConfigured());
    }
    @Test void publicBucketCannotBeUsedAsPrivateBucket() {
        MediaStorageProperties props = new MediaStorageProperties();
        props.setProvider("s3");
        props.getS3().setBucket("public");
        props.setPrivateBucket("public");
        props.getS3().setEndpoint("https://my.r2.cloudflarestorage.com");
        props.setPrivateAccessKey("private-key");
        props.setPrivateSecretKey("private-secret");
        var storage = new PrivateProfileMediaStorage(props);
        assertFalse(storage.isConfigured());
    }
}
