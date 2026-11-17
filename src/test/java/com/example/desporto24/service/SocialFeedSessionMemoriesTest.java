package com.example.desporto24.service;

import com.example.desporto24.enumeration.SessionAudience;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.session.SessionParticipant;
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
    private final SessionParticipantRepository participants=mock(SessionParticipantRepository.class);
    private final SocialFeedService service=new SocialFeedService(posts,follows,likes,comments,
            mock(SocialPostCommentLikeRepository.class),profiles,sessions,
            participants,blocks);

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
    private void confirmed(long sessionId,Profile... authors) {
        List<SessionParticipant> roster=java.util.Arrays.stream(authors).map(author->{
            var entry=mock(SessionParticipant.class);
            when(entry.getProfile()).thenReturn(author);
            return entry;
        }).toList();
        when(participants.findBySessionIdAndStateOrderByJoinedAtAsc(
                sessionId,ParticipantState.ACTIVE)).thenReturn(roster);
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
        confirmed(7,publicAuthor,privateAuthor,blockedAuthor);
        when(posts.findSessionMemories(eq(7L),anySet(),any(Pageable.class))).thenReturn(List.of(
                post(11,publicAuthor,game),post(12,privateAuthor,game),post(13,blockedAuthor,game)));
        when(blocks.isBlockedBetween(viewer,blockedAuthor)).thenReturn(true);
        when(likes.countByPostIds(anySet())).thenReturn(List.of());
        when(comments.countByPostIds(anySet())).thenReturn(List.of());
        when(likes.findLikedPostIds(anySet(),eq(1L))).thenReturn(java.util.Set.of());
        var result=service.sessionMemories(7L,"viewer");
        assertEquals(1,result.size());
        assertEquals(11L,result.get(0).postId());
        verify(posts).findSessionMemories(eq(7L),argThat(ids->ids.contains(2L)&&ids.contains(3L)&&ids.contains(4L)),argThat(page->page.getPageSize()==30));
    }

    @Test
    void followerMaySeePrivateAuthorsOwnMemoriesButNeverPrivateGames() {
        var viewer=player(1,"viewer",false);
        var author=player(3,"private",true);
        viewer(viewer);
        var game=session(7,SessionAudience.PUBLIC);
        confirmed(7,author);
        when(posts.findSessionMemories(eq(7L),anySet(),any(Pageable.class)))
                .thenReturn(List.of(post(12,author,game)));
        when(follows.existsByFollowerIdAndFollowingId(1L,3L)).thenReturn(true);
        when(likes.countByPostIds(anySet())).thenReturn(List.of());
        when(comments.countByPostIds(anySet())).thenReturn(List.of());
        when(likes.findLikedPostIds(anySet(),eq(1L))).thenReturn(java.util.Set.of());
        assertEquals(1,service.sessionMemories(7L,"viewer").size());
    }

    @Test
    void strangerCannotAddMomentsToAnotherTeamsGameAlbumByGuessingSessionId() {
        var viewer=player(1,"viewer",false);
        var genuine=player(2,"attendee",false);
        var stranger=player(8,"outsider",false);
        viewer(viewer);
        var game=session(7,SessionAudience.PUBLIC);
        confirmed(7,genuine);
        when(posts.findSessionMemories(eq(7L),anySet(),any(Pageable.class))).thenReturn(List.of(
                post(21,stranger,game),post(22,genuine,game)));
        when(likes.countByPostIds(anySet())).thenReturn(List.of());
        when(comments.countByPostIds(anySet())).thenReturn(List.of());
        when(likes.findLikedPostIds(anySet(),eq(1L))).thenReturn(java.util.Set.of());
        var album=service.sessionMemories(7L,"viewer");
        assertEquals(1,album.size());
        assertEquals(22L,album.get(0).postId());
    }

    @Test
    void organiserCanContributeWithoutBeingASeparateRosterMember() {
        var viewer=player(1,"viewer",false);
        var organiser=player(2,"organiser",false);
        viewer(viewer);
        var game=session(7,SessionAudience.PUBLIC);
        game.setProfile(organiser);
        when(posts.findSessionMemories(eq(7L),anySet(),any(Pageable.class)))
                .thenReturn(List.of(post(23,organiser,game)));
        when(likes.countByPostIds(anySet())).thenReturn(List.of());
        when(comments.countByPostIds(anySet())).thenReturn(List.of());
        when(likes.findLikedPostIds(anySet(),eq(1L))).thenReturn(java.util.Set.of());
        assertEquals(1,service.sessionMemories(7L,"viewer").size());
    }
}
