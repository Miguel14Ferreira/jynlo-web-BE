package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SportsTeamReactionServiceTest {
    private final SportsTeamRepository teams = mock(SportsTeamRepository.class);
    private final SportsTeamMemberRepository members = mock(SportsTeamMemberRepository.class);
    private final SportsTeamContentRepository messages = mock(SportsTeamContentRepository.class);
    private final SportsTeamContentReactionRepository reactions =
            mock(SportsTeamContentReactionRepository.class);
    private final ProfileRepository profiles = mock(ProfileRepository.class);
    private final SportsTeamReactionService service =
            new SportsTeamReactionService(teams, members, messages, reactions, profiles);
    private Profile captain;
    private Profile player;
    private SportsTeam team;
    private SportsTeamContent message;

    private Profile profile(long id, String username) {
        var profile = new Profile();
        profile.setId(id);
        profile.setUsername(username);
        return profile;
    }

    @BeforeEach
    void setup() {
        captain = profile(1L, "captain");
        player = profile(2L, "player");
        team = SportsTeam.builder().id(42L).owner(captain)
                .chatEnabled(true).feedEnabled(true).build();
        message = SportsTeamContent.builder().id(99L).team(team)
                .author(captain).kind(SportsTeamContent.Kind.CHAT)
                .body("Vamos jogar").createdAt(Instant.now()).build();
        when(teams.findById(42L)).thenReturn(Optional.of(team));
        when(profiles.findUserByUsername("captain")).thenReturn(Optional.of(captain));
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
        when(messages.findById(99L)).thenReturn(Optional.of(message));
    }

    @Test
    void invitedPlayerCannotReadOrReact() {
        when(members.findByTeamIdAndProfileId(42L, 2L)).thenReturn(Optional.of(
                SportsTeamMember.builder().team(team).profile(player)
                        .status(SportsTeamMember.Status.INVITED).build()));
        assertThrows(AccessDeniedException.class, () ->
                service.summaries(42L, SportsTeamContent.Kind.CHAT, List.of(99L), "player"));
        assertThrows(AccessDeniedException.class, () ->
                service.set(42L, SportsTeamContent.Kind.CHAT, 99L, "👍", "player"));
        verifyNoInteractions(reactions);
    }

    @Test
    void rejectsInvalidEmojiAndCrossTeamMessage() {
        assertThrows(ResponseStatusException.class, () ->
                service.set(42L, SportsTeamContent.Kind.CHAT, 99L, "<script>", "captain"));
        assertThrows(ResponseStatusException.class, () ->
                service.set(42L, SportsTeamContent.Kind.CHAT, 99L, null, "captain"));
        message.setTeam(SportsTeam.builder().id(50L).build());
        assertThrows(ResponseStatusException.class, () ->
                service.set(42L, SportsTeamContent.Kind.CHAT, 99L, "👍", "captain"));
        verifyNoInteractions(reactions);
    }

    @Test
    void repeatedReactionIsIdempotentAndNewChoiceReplacesOldOne() {
        var previous = SportsTeamContentReaction.builder().id(7L).content(message)
                .profile(captain).emoji("👍").createdAt(Instant.now()).build();
        when(reactions.findByContent_IdAndProfile_Id(99L, 1L))
                .thenReturn(Optional.of(previous));
        service.set(42L, SportsTeamContent.Kind.CHAT, 99L, "👍", "captain");
        verify(reactions, never()).saveAndFlush(any());
        service.set(42L, SportsTeamContent.Kind.CHAT, 99L, "🔥", "captain");
        assertEquals("🔥", previous.getEmoji());
        verify(reactions).saveAndFlush(previous);
    }

    @Test
    void batchedSummaryTracksCountsAndCurrentMembersChoice() {
        when(messages.findAllById(any())).thenReturn(List.of(message));
        var mine = SportsTeamContentReaction.builder().content(message).profile(captain)
                .emoji("👍").createdAt(Instant.now()).build();
        var other = SportsTeamContentReaction.builder().content(message).profile(player)
                .emoji("👍").createdAt(Instant.now()).build();
        when(reactions.findByContent_IdIn(any())).thenReturn(List.of(mine, other));
        var results = service.summaries(42L, SportsTeamContent.Kind.CHAT,
                List.of(99L), "captain");
        assertEquals(2L, results.get(99L).counts().get("👍"));
        assertEquals("👍", results.get(99L).mine());
    }

    @Test
    void reactionsCannotBeFetchedWithUnboundedListsOrForDisabledChat() {
        assertThrows(ResponseStatusException.class, () ->
                service.summaries(42L, SportsTeamContent.Kind.CHAT,
                        java.util.Collections.nCopies(101, 99L), "captain"));
        team.setChatEnabled(false);
        assertThrows(ResponseStatusException.class, () ->
                service.set(42L, SportsTeamContent.Kind.CHAT, 99L, "👍", "captain"));
    }
}
