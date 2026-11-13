package com.example.desporto24.controllers;

import com.example.desporto24.service.SocialFeedService;
import com.example.desporto24.service.SocialPostInteractionService;
import com.example.desporto24.service.SocialStoryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Regression: scalar multipart values are HTML form fields rather than JSON parts.
 * Browsers append plain text without a JSON media type, including text geometry.
 */
class SocialMultipartBindingTest {
    private final UsernamePasswordAuthenticationToken actor =
            new UsernamePasswordAuthenticationToken("miguel", "test");

    @Test
    void storyMultipartBindsGeometryAsOrdinaryFormFields() throws Exception {
        SocialStoryService stories = mock(SocialStoryService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SocialStoryController(stories)).build();

        mvc.perform(multipart("/api/stories")
                .file(new MockMultipartFile("image", "story.jpg", "image/jpeg", new byte[]{1}))
                .param("caption", "Momento")
                .param("textPosition", "bottom")
                .param("textScale", "medium")
                .param("textStyle", "pill")
                .param("showTime", "false")
                .param("locationLabel", "Benfica")
                .param("textX", "45")
                .param("textY", "55")
                .param("textRotation", "12")
                .param("textZoom", "1.1")
                .principal(actor)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(stories).create(eq("miguel"), isNull(), eq("Momento"),
                any(MockMultipartFile.class), isNull(), eq("bottom"), eq("medium"),
                eq("pill"), isNull(), eq(false), eq("Benfica"), isNull(),
                eq(45.0), eq(55.0), eq(12.0), eq(1.1));
    }

    @Test
    void editedGalleryMultipartBindsTextFilesAndReplaceFlag() throws Exception {
        SocialFeedService feed = mock(SocialFeedService.class);
        SocialPostInteractionService interactions = mock(SocialPostInteractionService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SocialFeedController(feed, interactions)).build();

        mvc.perform(multipart("/api/feed/posts/91/media")
                .file(new MockMultipartFile("files", "replacement.jpg", "image/jpeg", new byte[]{1, 2}))
                .param("content", "Depois do jogo")
                .param("location", "Benfica")
                .param("replaceMedia", "true")
                .with(request -> {request.setMethod("PUT"); return request;})
                .principal(actor)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(feed).editPostWithMedia(eq(91L), eq("miguel"),
                eq("Depois do jogo"), eq("Benfica"), argThat(files -> files.size() == 1), eq(true));
    }

    @Test
    void postMultipartBindsContentAndLocationAsOrdinaryFormFields() throws Exception {
        SocialFeedService feed = mock(SocialFeedService.class);
        SocialPostInteractionService interactions = mock(SocialPostInteractionService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new SocialFeedController(feed, interactions)).build();

        mvc.perform(multipart("/api/feed/posts")
                .file(new MockMultipartFile("image", "photo.jpg", "image/jpeg", new byte[]{1}))
                .param("content", "")
                .param("location", "Benfica")
                .principal(actor)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        verify(feed).createPostWithMedia(eq("miguel"), eq(""),
                any(MockMultipartFile.class), isNull(), isNull(), eq("Benfica"));
    }
}
