package com.example.desporto24.service;

import com.example.desporto24.enumeration.SessionAudience;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.social.SocialPost;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import com.example.desporto24.repository.social.*;
import com.example.desporto24.repository.session.SessionParticipantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SocialFeedSessionMemoriesTest {
    private final SocialPostRepository posts=mock(SocialPostRepository.class);
    private final ProfileRepository profiles=mock(ProfileRepository.class);
    private final SessionRepository sessions=mock(SessionRepository.class);
    private final ProfileFollowRepository follows=mock(ProfileFollowRepository.class);
    private final SocialPostLikeRepository likes=mock(SocialPostLikeRepository.class);
    private final SocialPostCommentRepository comments=mock(SocialPostCommentRepository.class);
    private final UserBlockService blocks=mock(UserBlockService.class);
    private final SocialFeedService service=new SocialFeedService(posts,follows,likes,comments,
            mock(SocialPostCommentLikeRepository.class),profiles,sessions,
            mock(SessionParticipantRepository.class),blocks);

    private Profile player(long id,String username,boolean privateProfile) {
        var p=new Profile();p.setId(id);p.setUsername(username);
        p.setPrivateProfile(privateProfile);return p;
    }
    private Session session(long id,SessionAudience audience) {
        var s=new Session();s.setId(id);s.setAudience(audience);s.setEnabled(true);
        when(sessions.findById(id)).thenReturn(Optional.of(s));return s;
    }
    private SocialPost post(long id,Profile author,Session session) {
        return SocialPost.builder().id(id).author(author).session(session)
                .content("Fotografias do jogo").createdAt(Instant.now()).build();
    }
    private void viewer(Profile profile) {
        when(profiles.findUserByUsernameIgnoreCase(profile.getUsername())).thenReturn(Optional.of(profile));
    }

    @Test
    void privateAndCommunityGamesNeverQueryOrExposeTheirPosts() {
        viewer(player(1,"viewer",false));
        session(7,SessionAudience.COMMUNITY_ONLY);
        assertThrows(ResponseStatusException.class,()->service.sessionMemories(7L,"viewer"));
        verifyNoInteractions(posts);
    }

    @Test
    void publicMemoriesRespectPrivateProfilesAndMutualBlocks() {
        var viewer=player(1,"viewer",false);
        var publicAuthor=player(2,"public",false);
        var privateAuthor=player(3,"private",true);
        var blockedAuthor=player(4,"blocked",false);
        viewer(viewer);
        var game=session(7,SessionAudience.PUBLIC);
        when(posts.findSessionMemories(eq(7L),any(Pageable.class))).thenReturn(List.of(
                post(11,publicAuthor,game),post(12,privateAuthor,game),post(13,blockedAuthor,game)));
        when(blocks.isBlockedBetween(viewer,blockedAuthor)).thenReturn(true);
        when(likes.countByPostIds(anySet())).thenReturn(List.of());
        when(comments.countByPostIds(anySet())).thenReturn(List.of());
        when(likes.findLikedPostIds(anySet(),eq(1L))).thenReturn(java.util.Set.of());
        var result=service.sessionMemories(7L,"viewer");
        assertEquals(1,result.size());
        assertEquals(11L,result.get(0).postId());
        verify(posts).findSessionMemories(eq(7L),argThat(page->page.getPageSize()==30));
    }

    @Test
    void followerMaySeePrivateAuthorsOwnMemoriesButNeverPrivateGames() {
        var viewer=player(1,"viewer",false);
        var author=player(3,"private",true);
        viewer(viewer);
        var game=session(7,SessionAudience.PUBLIC);
        when(posts.findSessionMemories(eq(7L),any(Pageable.class)))
                .thenReturn(List.of(post(12,author,game)));
        when(follows.existsByFollowerIdAndFollowingId(1L,3L)).thenReturn(true);
        when(likes.countByPostIds(anySet())).thenReturn(List.of());
        when(comments.countByPostIds(anySet())).thenReturn(List.of());
        when(likes.findLikedPostIds(anySet(),eq(1L))).thenReturn(java.util.Set.of());
        assertEquals(1,service.sessionMemories(7L,"viewer").size());
    }
}
