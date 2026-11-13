package com.example.desporto24.controllers;

import com.example.desporto24.model.dto.community.CommunityTimelineEntryResponse;
import com.example.desporto24.service.CommunityTimelineService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Regression for the browser's FormData POST /api/communities/{id}/posts.
 * No real account, storage or external provider is involved.
 */
class CommunityTimelineMultipartControllerTest {
    private final CommunityTimelineService service = mock(CommunityTimelineService.class);
    private final org.springframework.test.web.servlet.MockMvc mvc =
            MockMvcBuilders.standaloneSetup(new CommunityTimelineController(service)).build();

    private MockMultipartFile content(String text) {
        return new MockMultipartFile("content", "", "text/plain",
                text.getBytes(StandardCharsets.UTF_8));
    }

    private UsernamePasswordAuthenticationToken member() {
        return new UsernamePasswordAuthenticationToken("ana", null);
    }

    @Test
    void browserCanPublishOneImageWithoutAnyText() throws Exception {
        when(service.postWithMedia(eq(7L), nullable(String.class), any(), isNull(), eq("ana")))
                .thenReturn(new CommunityTimelineEntryResponse(
                        12L, 7L, "POST", "ana", "", Instant.now()));
        var photo = new MockMultipartFile("image", "MF.jpg", "image/jpeg", new byte[]{1, 2});

        mvc.perform(multipart("/api/communities/7/posts")
                        .file(content("")).file(photo).principal(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12));
        verify(service).postWithMedia(eq(7L), nullable(String.class),
                argThat(file -> "MF.jpg".equals(file.getOriginalFilename())),
                isNull(), eq("ana"));
        verify(service, never()).postWithAlbum(anyLong(), any(), anyList(), any());
    }

    @Test
    void browserSendsTwoPhotosAsOneAlbumInOriginalOrder() throws Exception {
        when(service.postWithAlbum(eq(7L), eq("Jogo"), anyList(), eq("ana")))
                .thenReturn(new CommunityTimelineEntryResponse(
                        13L, 7L, "POST", "ana", "Jogo", Instant.now()));
        var first = new MockMultipartFile("files", "1.jpg", "image/jpeg", new byte[]{1});
        var second = new MockMultipartFile("files", "2.png", "image/png", new byte[]{2});

        mvc.perform(multipart("/api/communities/7/posts")
                        .file(content("Jogo")).file(first).file(second).principal(member()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(13));
        verify(service).postWithAlbum(eq(7L), eq("Jogo"), argThat(files ->
                files.size() == 2
                && "1.jpg".equals(files.get(0).getOriginalFilename())
                && "2.png".equals(files.get(1).getOriginalFilename())), eq("ana"));
        verify(service, never()).postWithMedia(anyLong(), any(), any(), any(), any());
    }
}
