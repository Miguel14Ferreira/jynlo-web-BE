package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.SocialPostRepository;
import com.example.desporto24.repository.social.SocialPostLikeRepository;
import com.example.desporto24.repository.social.SocialPostCommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialProfilePostsTest {
    @Mock private SocialPostRepository posts;
    @Mock private SocialPostLikeRepository likes;
    @Mock private SocialPostCommentRepository comments;
    @Mock private ProfileRepository profiles;
    @Mock private UserBlockService blocks;
    @InjectMocks private SocialFeedService service;

    @Test
    void profileShowsOnlyAuthoredPublicationsAndSupportsOlderPages() {
        Profile author = new Profile();
        author.setId(12L);
        author.setUsername("ana");
        Profile viewer = new Profile();
        viewer.setId(15L);
        viewer.setUsername("joao");
        when(profiles.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(author));
        when(profiles.findUserByUsernameIgnoreCase("joao")).thenReturn(Optional.of(viewer));
        SocialPost post = SocialPost.builder()
                .id(99L).author(author).content("Partilha de treino")
                .createdAt(Instant.now()).build();
        when(posts.findByAuthorIdAndActivityTypeIsNullOrderByCreatedAtDesc(eq(12L),any(Pageable.class)))
                .thenReturn(List.of(post));
        when(likes.countByPostIds(anySet())).thenReturn(java.util.Collections.singletonList(new Object[] {99L, 4L}));
        when(comments.countByPostIds(anySet())).thenReturn(java.util.Collections.singletonList(new Object[] {99L, 2L}));
        when(likes.findLikedPostIds(anySet(), eq(15L))).thenReturn(java.util.Set.of(99L));

        var page = service.profilePosts("ana", "joao", 2);
        assertEquals(1, page.size());
        assertEquals(99L, page.get(0).postId());
        assertEquals(4L, page.get(0).likeCount());
        assertEquals(2L, page.get(0).commentCount());
        assertTrue(page.get(0).likedByMe());
        verify(likes).countByPostIds(java.util.Set.of(99L));
        verify(comments).countByPostIds(java.util.Set.of(99L));
        verify(likes).findLikedPostIds(java.util.Set.of(99L), 15L);
        verify(likes, never()).countByPostId(anyLong());
        verify(comments, never()).countByPostId(anyLong());
        verify(posts).findByAuthorIdAndActivityTypeIsNullOrderByCreatedAtDesc(
                eq(12L),argThat(p -> p.getPageNumber()==2 && p.getPageSize()==20));
    }

    @Test
    void blockedProfilesCannotReadAuthoredPostsInEitherDirection() {
        Profile author = new Profile();
        author.setId(12L);
        author.setUsername("ana");
        Profile viewer = new Profile();
        viewer.setId(15L);
        viewer.setUsername("joao");
        when(profiles.findUserByUsernameIgnoreCase("ana")).thenReturn(Optional.of(author));
        when(profiles.findUserByUsernameIgnoreCase("joao")).thenReturn(Optional.of(viewer));
        when(blocks.isBlockedBetween(viewer, author)).thenReturn(true);

        var exception = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> service.profilePosts("ana", "joao", 0));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(posts, likes, comments);
    }

    @Test
    void profilePaginationRejectsNegativeAndExcessivePages() {
        assertThrows(IllegalArgumentException.class, () -> service.profilePosts("ana","joao",-1));
        assertThrows(IllegalArgumentException.class, () -> service.profilePosts("ana","joao",1001));
        verifyNoInteractions(posts);
    }
}
