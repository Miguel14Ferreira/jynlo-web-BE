package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.service.profile.FirstStepsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirstStepsServiceTest {

    @Mock private ProfileRepository profiles;
    @InjectMocks private FirstStepsService service;

    private Profile profile(String sports, String location) {
        Profile p = new Profile();
        p.setUsername("ana");
        p.setFavoriteSports(sports);
        p.setLocation(location);
        return p;
    }

    @Test
    void emptySportsMeansFirstStepsAreNeeded() {
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(profile(null, null)));
        assertTrue(service.get("ana").needed());
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(profile("Padel, Ténis", "Porto")));
        var view = service.get("ana");
        assertFalse(view.needed());
        assertEquals(List.of("Padel", "Ténis"), view.favoriteSports());
    }

    @Test
    void savesOnlySportsAndCity() {
        Profile p = profile(null, "Braga");
        p.setEmail("ana@example.com");
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(p));
        when(profiles.save(any())).thenAnswer(i -> i.getArgument(0));

        var view = service.save("ana", new FirstStepsService.Update(Arrays.asList(" Padel ", "Padel", null, "Ténis"), " Porto\n"));

        assertEquals("Padel,Ténis", p.getFavoriteSports());
        assertEquals("Porto", p.getLocation());
        assertEquals("ana@example.com", p.getEmail());
        assertFalse(view.needed());
    }

    @Test
    void blankCityKeepsThePreviousOne() {
        Profile p = profile(null, "Braga");
        when(profiles.findUserByUsername("ana")).thenReturn(Optional.of(p));
        when(profiles.save(any())).thenAnswer(i -> i.getArgument(0));
        service.save("ana", new FirstStepsService.Update(List.of("Padel"), "  "));
        assertEquals("Braga", p.getLocation());
    }

    @Test
    void rejectsUnknownEmptyOrTooManySports() {
        assertThrows(ResponseStatusException.class, () -> service.save("ana", new FirstStepsService.Update(List.of(), "Porto")));
        assertThrows(ResponseStatusException.class, () -> service.save("ana", new FirstStepsService.Update(null, "Porto")));
        assertThrows(ResponseStatusException.class, () -> service.save("ana", new FirstStepsService.Update(List.of("Quidditch"), "Porto")));
        assertThrows(ResponseStatusException.class, () -> service.save("ana",
                new FirstStepsService.Update(Collections.nCopies(11, "x").stream().map(s -> s + Math.random()).toList(), "Porto")));
        assertThrows(ResponseStatusException.class, () -> service.save("ana", new FirstStepsService.Update(List.of("Padel"), "x".repeat(121))));
        verify(profiles, never()).save(any());
    }
}
