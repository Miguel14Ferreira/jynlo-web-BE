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
}
