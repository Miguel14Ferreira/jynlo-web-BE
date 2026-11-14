package com.example.desporto24.service;

import com.example.desporto24.storage.PrivateChatFileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrivateChatAttachmentBatchSecurityTest {
    private MultipartFile file(long bytes) {
        MultipartFile mock = mock(MultipartFile.class);
        when(mock.isEmpty()).thenReturn(false);
        when(mock.getSize()).thenReturn(bytes);
        return mock;
    }

    @Test void rejectsFileLargerThanIndividualLimitBeforeReadingBytes() throws java.io.IOException {
        MultipartFile huge = file(PrivateChatFileStorage.MAX_BYTES + 1);
        assertThrows(ResponseStatusException.class,
                () -> PrivateChatAttachmentBatch.validate(List.of(huge)));
        verify(huge, never()).getBytes();
    }

    @Test void rejectsOverflowedAdvertisedSizes() throws java.io.IOException {
        MultipartFile first = file(Long.MAX_VALUE);
        MultipartFile second = file(Long.MAX_VALUE);
        assertThrows(ResponseStatusException.class,
                () -> PrivateChatAttachmentBatch.validate(List.of(first, second)));
        verify(first, never()).getBytes();
        verify(second, never()).getBytes();
    }

    @Test void rejectsAggregateSizeAboveLimit() {
        MultipartFile one = file(20L * 1024 * 1024);
        MultipartFile two = file(20L * 1024 * 1024);
        MultipartFile three = file(1);
        assertThrows(ResponseStatusException.class,
                () -> PrivateChatAttachmentBatch.validate(List.of(one,two,three)));
    }

    @Test void acceptsExactlyFortyMegabytesAcrossTwoFiles() {
        assertDoesNotThrow(() -> PrivateChatAttachmentBatch.validate(
                List.of(file(20L * 1024 * 1024),file(20L * 1024 * 1024))));
    }

    @Test void rejectsZeroAndNegativeAdvertisedSize() {
        assertThrows(ResponseStatusException.class,
                () -> PrivateChatAttachmentBatch.validate(List.of(file(0))));
        assertThrows(ResponseStatusException.class,
                () -> PrivateChatAttachmentBatch.validate(List.of(file(-1))));
    }
}
