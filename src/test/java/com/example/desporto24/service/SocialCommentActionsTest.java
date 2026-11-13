package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.model.social.SocialPostComment;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.*;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import com.example.desporto24.repository.NotificationsRepository;
import com.example.desporto24.websocket.ChatRealtimeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SocialCommentActionsTest {
    private final SocialPostRepository posts=mock(SocialPostRepository.class);
    private final SocialPostLikeRepository postLikes=mock(SocialPostLikeRepository.class);
    private final SocialPostCommentRepository comments=mock(SocialPostCommentRepository.class);
    private final SocialPostCommentLikeRepository likes=mock(SocialPostCommentLikeRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final UserBlockService blocks=mock(UserBlockService.class);
    private SocialPostInteractionService service;
    private Profile author, stranger;
    private SocialPost post;
    private SocialPostComment comment;

    @BeforeEach void setup() {
        service=new SocialPostInteractionService(posts,postLikes,comments,likes,profiles,
            mock(UserReportService.class),mock(SessionParticipantRepository.class),
            mock(NotificationsRepository.class),mock(NotificationPreferencesService.class),
            blocks,mock(ChatRealtimeGateway.class));
        author=new Profile();author.setId(2L);author.setUsername("author");
        stranger=new Profile();stranger.setId(3L);stranger.setUsername("other");
        post=SocialPost.builder().id(44L).author(author).content("Post").createdAt(Instant.now()).build();
        comment=SocialPostComment.builder().id(55L).post(post).author(author)
            .content("Original").createdAt(Instant.now()).build();
        when(profiles.findUserByUsernameIgnoreCase("author")).thenReturn(Optional.of(author));
        when(profiles.findUserByUsernameIgnoreCase("other")).thenReturn(Optional.of(stranger));
        when(posts.findById(44L)).thenReturn(Optional.of(post));
        when(comments.findById(55L)).thenReturn(Optional.of(comment));
    }
    @Test void onlyOwnerCanEditAndOriginalTimestampRemains() {
        Instant before=comment.getCreatedAt();
        assertThrows(AccessDeniedException.class,()->service.editComment(44L,55L,"other","Alterado"));
        var response=service.editComment(44L,55L,"author"," Alterado ");
        assertEquals("Alterado",response.content());
        assertEquals(before,response.createdAt());
        assertNotNull(response.editedAt());
        verify(comments).save(comment);
    }
    @Test void invalidOrWrongPostEditsDoNotSave() {
        assertThrows(IllegalArgumentException.class,()->service.editComment(43L,55L,"author","Texto"));
        assertThrows(IllegalArgumentException.class,()->service.editComment(44L,55L,"author"," "));
        verify(comments,never()).save(any());
    }
    @Test void duplicateLikeIsIdempotentAndRemovalIsAllowed() {
        when(likes.existsByCommentIdAndProfileId(55L,3L)).thenReturn(false,true,true,false);
        when(likes.countByCommentId(55L)).thenReturn(1L,1L,0L);
        service.setCommentLike(44L,55L,"other",true);
        service.setCommentLike(44L,55L,"other",true);
        service.setCommentLike(44L,55L,"other",false);
        verify(likes,times(1)).save(any());
        verify(likes,times(1)).deleteByCommentIdAndProfileId(55L,3L);
    }
}
