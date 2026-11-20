package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityFollowRepository;
import com.example.desporto24.repository.community.CommunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityFollowServiceTest {
    @Mock CommunityRepository communities;
    @Mock CommunityFollowRepository followers;
    @Mock ProfileRepository profiles;
    @InjectMocks CommunityFollowService service;

    @Test void privateCommunitiesAreNotFollowableOrDiscoverableThroughCounts() {
        Community group=new Community();
        group.setId(7L);
        group.setVisibility(CommunityVisibility.PRIVATE);
        when(communities.findById(7L)).thenReturn(Optional.of(group));
        assertThrows(ResponseStatusException.class,()->service.state(7L,"player"));
        assertThrows(ResponseStatusException.class,()->service.follow(7L,"player"));
        assertThrows(ResponseStatusException.class,()->service.unfollow(7L,"player"));
        verifyNoInteractions(followers);
    }

    @Test void publicFollowingDoesNotCreateMembershipAndRepeatedRequestsAreIdempotent() {
        Community group=new Community();
        group.setId(7L);
        group.setVisibility(CommunityVisibility.PUBLIC);
        Profile player=new Profile();
        player.setId(31L);
        player.setEnabled(true);
        when(communities.findById(7L)).thenReturn(Optional.of(group));
        when(profiles.findUserByUsername("player")).thenReturn(Optional.of(player));
        when(followers.existsByCommunityIdAndFollowerId(7L,31L)).thenReturn(true);
        when(followers.countByCommunityId(7L)).thenReturn(4L);
        var state=service.follow(7L,"player");
        assertEquals(4L,state.count());
        assertTrue(state.following());
        verify(followers,never()).saveAndFlush(any());
    }
}
