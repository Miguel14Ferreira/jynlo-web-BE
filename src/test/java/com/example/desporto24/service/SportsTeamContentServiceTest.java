package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamContent;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SportsTeamContentServiceTest {
    private final SportsTeamRepository teams = mock(SportsTeamRepository.class);
    private final SportsTeamMemberRepository members = mock(SportsTeamMemberRepository.class);
    private final SportsTeamContentRepository entries = mock(SportsTeamContentRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SportsTeamContentService service =
        new SportsTeamContentService(teams, members, entries, profiles);
    private SportsTeam team;
    private Profile owner;
    private Profile player;

    @BeforeEach
    void setup() {
        owner = profile(1L,"captain");
        player = profile(2L,"player");
        team=SportsTeam.builder().id(42L).owner(owner)
            .chatEnabled(true).feedEnabled(true).build();
        when(teams.findById(42L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(owner));
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
    }

    private Profile profile(long id, String username) {
        var p=new Profile();p.setId(id);p.setUsername(username);return p;
    }

    @Test
    void disabledOrSuspendedAccountsCannotUsePrivateChatEvenWithActiveMembership() {
        when(members.findByTeamIdAndProfileId(42L,2L)).thenReturn(Optional.of(
            SportsTeamMember.builder().team(team).profile(player)
                .status(SportsTeamMember.Status.ACTIVE).build()));

        player.setEnabled(false);
        assertThrows(AccessDeniedException.class, () ->
            service.recent(42L, SportsTeamContent.Kind.CHAT, "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.post(42L, SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("blocked"), "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.attachmentsAvailable(42L, SportsTeamContent.Kind.CHAT, "player"));

        player.setEnabled(true);
        player.setModerationSuspendedAt(Instant.now());
        assertThrows(AccessDeniedException.class, () ->
            service.search(42L, SportsTeamContent.Kind.CHAT, "private", "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.download(42L, SportsTeamContent.Kind.CHAT, 4L, 0, "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.delete(42L, SportsTeamContent.Kind.CHAT, 4L, "player"));
        verifyNoInteractions(entries);
    }

    @Test
    void suspendedCaptainAlsoLosesPrivateChatAccess() {
        owner.setModerationSuspendedAt(Instant.now());
        assertThrows(AccessDeniedException.class, () ->
            service.recent(42L, SportsTeamContent.Kind.CHAT, "captain"));
        assertThrows(AccessDeniedException.class, () ->
            service.post(42L, SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("blocked"), "captain"));
        verifyNoInteractions(entries);
    }

    @Test
    void invitedMembersCannotReadOrPostAnyPrivateTeamContent() {
        when(members.findByTeamIdAndProfileId(42L,2L)).thenReturn(Optional.of(
            SportsTeamMember.builder().team(team).profile(player)
                .status(SportsTeamMember.Status.INVITED).build()));
        for(var kind:SportsTeamContent.Kind.values()) {
            assertThrows(AccessDeniedException.class,()->service.recent(42L,kind,"player"));
            assertThrows(AccessDeniedException.class,()->service.post(42L,kind,
                new SportsTeamContentService.Submit("hello"),"player"));
        }
        verifyNoInteractions(entries);
    }

    @Test
    void activeMemberCanReadChatAndPostButNotWhenDisabled() {
        when(members.findByTeamIdAndProfileId(42L,2L)).thenReturn(Optional.of(
            SportsTeamMember.builder().team(team).profile(player)
                .status(SportsTeamMember.Status.ACTIVE).build()));
        when(entries.findByTeamIdAndKindOrderByIdDesc(eq(42L),eq(SportsTeamContent.Kind.CHAT),
                any(Pageable.class))).thenReturn(List.of());
        assertTrue(service.recent(42L,SportsTeamContent.Kind.CHAT,"player").isEmpty());
        when(entries.saveAndFlush(any())).thenAnswer(invocation->{
            SportsTeamContent row=invocation.getArgument(0);
            row.setId(15L);return row;
        });
        var created=service.post(42L,SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("  Olá equipa!  "),"player");
        assertEquals("Olá equipa!",created.body());
        assertEquals("player",created.username());
        assertEquals(42L,created.teamId());
        team.setChatEnabled(false);
        assertThrows(ResponseStatusException.class,()->
            service.post(42L,SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("test"),"player"));
    }

    @Test
    void rejectsEmptyOrOversizedPostsAndNeverWrites() {
        assertThrows(ResponseStatusException.class,()->
            service.post(42L,SportsTeamContent.Kind.FEED,new SportsTeamContentService.Submit("  "),"captain"));
        assertThrows(ResponseStatusException.class,()->
            service.post(42L,SportsTeamContent.Kind.FEED,
                new SportsTeamContentService.Submit("x".repeat(2001)),"captain"));
        verifyNoInteractions(entries);
    }

    @Test
    void chronologicalReadIsBoundedToOneHundredMessages() {
        var older=SportsTeamContent.builder().id(10L).team(team).author(owner)
                .kind(SportsTeamContent.Kind.CHAT).body("primeira").createdAt(Instant.now()).build();
        var newer=SportsTeamContent.builder().id(11L).team(team).author(owner)
                .kind(SportsTeamContent.Kind.CHAT).body("segunda").createdAt(Instant.now()).build();
        when(entries.findByTeamIdAndKindOrderByIdDesc(eq(42L),eq(SportsTeamContent.Kind.CHAT),
            any(Pageable.class))).thenReturn(List.of(newer,older));
        var results=service.recent(42L,SportsTeamContent.Kind.CHAT,"captain");
        assertEquals(10L,results.get(0).id());
        assertEquals(11L,results.get(1).id());
        verify(entries).findByTeamIdAndKindOrderByIdDesc(eq(42L),
                eq(SportsTeamContent.Kind.CHAT),argThat(page->page.getPageSize()==100));
    }

    @Test
    void teamReplyCannotReferenceAnotherTeamsOrDeletedMessage() {
        when(entries.findById(99L)).thenReturn(Optional.of(SportsTeamContent.builder()
                .id(99L).team(SportsTeam.builder().id(100L).build())
                .kind(SportsTeamContent.Kind.CHAT).build()));
        assertThrows(ResponseStatusException.class, () -> service.post(42L,
                SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("reply",99L),"captain"));
        verify(entries,never()).saveAndFlush(any());
    }

    @Test
    void authorCanEditOwnMessageButCaptainCannotEditAnotherPlayersText() {
        var message = SportsTeamContent.builder().id(15L).team(team).author(player)
                .body("hello").kind(SportsTeamContent.Kind.CHAT).createdAt(Instant.now()).build();
        when(entries.findById(15L)).thenReturn(Optional.of(message));
        when(entries.saveAndFlush(any())).thenAnswer(i->i.getArgument(0));
        assertThrows(AccessDeniedException.class, () -> service.edit(42L,
                SportsTeamContent.Kind.CHAT, 15L,
                new SportsTeamContentService.Submit("rewritten"),"captain"));
        when(members.findByTeamIdAndProfileId(42L,2L)).thenReturn(Optional.of(
                SportsTeamMember.builder().team(team).profile(player)
                        .status(SportsTeamMember.Status.ACTIVE).build()));
        var result=service.edit(42L,SportsTeamContent.Kind.CHAT,15L,
                new SportsTeamContentService.Submit("edited"),"player");
        assertEquals("edited",result.body());
        assertNotNull(result.editedAt());
        assertTrue(result.canEdit());
    }

    @Test
    void captainCanSoftDeleteButNeverExposeDeletedText() {
        var message=SportsTeamContent.builder().id(15L).team(team).author(player)
                .body("sensitive text").kind(SportsTeamContent.Kind.CHAT).createdAt(Instant.now()).build();
        when(entries.findById(15L)).thenReturn(Optional.of(message));
        when(entries.saveAndFlush(any())).thenAnswer(i->i.getArgument(0));
        service.delete(42L,SportsTeamContent.Kind.CHAT,15L,"captain");
        assertNotNull(message.getDeletedAt());
        assertEquals("",message.getBody());
        assertThrows(ResponseStatusException.class, () -> service.delete(42L,
                SportsTeamContent.Kind.CHAT,15L,"captain"));
        when(entries.findByTeamIdAndKindOrderByIdDesc(eq(42L),
                eq(SportsTeamContent.Kind.CHAT),any(Pageable.class))).thenReturn(List.of(message));
        var result=service.recent(42L,SportsTeamContent.Kind.CHAT,"captain").get(0);
        assertTrue(result.deleted());
        assertEquals("",result.body());
        assertTrue(result.canManage());
        assertFalse(result.canEdit());
    }

    @Test
    void outsiderCannotEditOrDeleteAnyMessage() {
        var outsider=profile(3L,"outsider");
        when(profiles.findUserByUsername("outsider")).thenReturn(Optional.of(outsider));
        assertThrows(AccessDeniedException.class, () -> service.delete(42L,
                SportsTeamContent.Kind.CHAT,15L,"outsider"));
        assertThrows(AccessDeniedException.class, () -> service.edit(42L,
                SportsTeamContent.Kind.CHAT,15L,new SportsTeamContentService.Submit("x"),"outsider"));
        verify(entries,never()).findById(anyLong());
    }


    @Test
    void searchingRequiresMembershipAndNeverLeaksOtherTeamContent() {
        when(members.findByTeamIdAndProfileId(42L,2L)).thenReturn(Optional.of(
                SportsTeamMember.builder().team(team).profile(player)
                        .status(SportsTeamMember.Status.INVITED).build()));
        assertThrows(AccessDeniedException.class, () -> service.search(
                42L, SportsTeamContent.Kind.CHAT, "password", "player"));
        verify(entries, never()).searchVisible(anyLong(), any(), anyString(), any(Pageable.class));
    }

    @Test
    void searchRejectsShortLongOrControlQueriesAndBoundsResults() {
        for (String invalid : List.of("", "a", "a".repeat(81), "x\nsecret")) {
            assertThrows(ResponseStatusException.class, () -> service.search(
                    42L, SportsTeamContent.Kind.CHAT, invalid, "captain"));
        }
        var row = SportsTeamContent.builder().id(10L).team(team).author(owner)
                .kind(SportsTeamContent.Kind.CHAT).body("Olá equipa").createdAt(Instant.now()).build();
        when(entries.searchVisible(eq(42L), eq(SportsTeamContent.Kind.CHAT),
                eq("equipa"), any(Pageable.class))).thenReturn(List.of(row));
        var matches = service.search(42L, SportsTeamContent.Kind.CHAT, "  equipa  ", "captain");
        assertEquals(1, matches.size());
        assertEquals("Olá equipa", matches.get(0).body());
        verify(entries).searchVisible(eq(42L), eq(SportsTeamContent.Kind.CHAT), eq("equipa"),
                argThat(page -> page.getPageSize() == 100));
    }


    @Test
    void disablingChatRejectsAllPrivateOperationsButDoesNotDisableFeed() {
        when(members.findByTeamIdAndProfileId(42L, 2L)).thenReturn(Optional.of(
                SportsTeamMember.builder().team(team).profile(player)
                        .status(SportsTeamMember.Status.ACTIVE).build()));
        team.setChatEnabled(false);
        team.setFeedEnabled(true);

        assertThrows(ResponseStatusException.class, () ->
                service.recent(42L, SportsTeamContent.Kind.CHAT, "player"));
        assertThrows(ResponseStatusException.class, () ->
                service.search(42L, SportsTeamContent.Kind.CHAT, "message", "player"));
        assertThrows(ResponseStatusException.class, () ->
                service.post(42L, SportsTeamContent.Kind.CHAT,
                        new SportsTeamContentService.Submit("blocked"), "player"));
        assertThrows(ResponseStatusException.class, () ->
                service.attachmentsAvailable(42L, SportsTeamContent.Kind.CHAT, "player"));
        assertThrows(ResponseStatusException.class, () ->
                service.download(42L, SportsTeamContent.Kind.CHAT, 4L, 0, "player"));
        assertThrows(ResponseStatusException.class, () ->
                service.upload(42L, SportsTeamContent.Kind.CHAT, List.of(), "", null, "player"));

        verifyNoInteractions(entries);
        when(entries.findByTeamIdAndKindOrderByIdDesc(eq(42L),
                eq(SportsTeamContent.Kind.FEED), any(Pageable.class))).thenReturn(List.of());
        assertTrue(service.recent(42L, SportsTeamContent.Kind.FEED, "player").isEmpty());
    }

    @Test
    void removedMemberCannotReadSearchPostOrProbePrivateAttachments() {
        // A deleted membership is indistinguishable from an outsider to the private API.
        when(members.findByTeamIdAndProfileId(42L, 2L)).thenReturn(Optional.empty());
        assertThrows(AccessDeniedException.class, () ->
            service.recent(42L, SportsTeamContent.Kind.CHAT, "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.search(42L, SportsTeamContent.Kind.CHAT, "private", "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.post(42L, SportsTeamContent.Kind.CHAT,
                new SportsTeamContentService.Submit("retry"), "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.attachmentsAvailable(42L, SportsTeamContent.Kind.CHAT, "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.download(42L, SportsTeamContent.Kind.CHAT, 4L, 0, "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.upload(42L, SportsTeamContent.Kind.CHAT, List.of(), "", null, "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.edit(42L, SportsTeamContent.Kind.CHAT, 4L,
                new SportsTeamContentService.Submit("forbidden"), "player"));
        assertThrows(AccessDeniedException.class, () ->
            service.delete(42L, SportsTeamContent.Kind.CHAT, 4L, "player"));
        verifyNoInteractions(entries);
    }
}
