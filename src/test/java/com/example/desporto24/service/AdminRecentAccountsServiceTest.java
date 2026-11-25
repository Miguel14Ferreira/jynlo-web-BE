package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminRecentAccountsServiceTest {
    private Profile profile(long id, String username, String email, Instant joined, boolean confirmed) {
        Profile p = new Profile();
        p.setId(id);
        p.setUsername(username);
        p.setEmail(email);
        p.setJoinDate(Date.from(joined));
        p.setEnabled(confirmed);
        p.setNotLocked(true);
        return p;
    }

    @Test
    void flagsGeneratedEmailsMissingConfirmationAndBursts() {
        ProfileRepository repo = mock(ProfileRepository.class);
        Instant now = Instant.now();
        Profile fake = profile(1L, "freddieob", "freddieobrien.72965@gmail.com", now, true);
        Profile real = profile(2L, "maria", "maria.silva@gmail.com", now.minusSeconds(86_400), true);
        real.setProfilePicUrl("https://media.example/p.jpg");
        real.setLastLoginDate(new Date());
        when(repo.findByJoinDateAfterOrderByJoinDateDesc(any(), any())).thenReturn(List.of(fake, real));

        var result = new AdminRecentAccountsService(repo).recent(14);

        assertThat(result.get(0).flags()).contains("EMAIL_PADRAO_ALEATORIO", "SEM_FOTO", "NUNCA_ENTROU");
        assertThat(result.get(1).flags()).isEmpty();
    }

    @Test
    void threeSignupsWithinTenMinutesAreMarked() {
        ProfileRepository repo = mock(ProfileRepository.class);
        Instant now = Instant.now();
        when(repo.findByJoinDateAfterOrderByJoinDateDesc(any(), any())).thenReturn(List.of(
                profile(1L, "a", "a@x.pt", now, false),
                profile(2L, "b", "b@x.pt", now.minusSeconds(120), false),
                profile(3L, "c", "c@x.pt", now.minusSeconds(240), false)));

        var result = new AdminRecentAccountsService(repo).recent(14);

        assertThat(result).allSatisfy(r -> assertThat(r.flags()).contains("VARIOS_REGISTOS_SEGUIDOS", "EMAIL_NAO_CONFIRMADO"));
    }
}
