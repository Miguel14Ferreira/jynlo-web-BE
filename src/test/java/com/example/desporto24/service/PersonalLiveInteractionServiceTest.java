package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalLiveInteractionServiceTest {
  private final PersonalLiveEventRepository lives=mock(PersonalLiveEventRepository.class);
  private final PersonalLiveChatMessageRepository chat=mock(PersonalLiveChatMessageRepository.class);
  private final PersonalLiveGuestRequestRepository guests=mock(PersonalLiveGuestRequestRepository.class);
  private final PersonalLiveMuteRepository mutes=mock(PersonalLiveMuteRepository.class);
  private final ProfileRepository profiles=mock(ProfileRepository.class);
  private final UserBlockService blocks=mock(UserBlockService.class);
  private final DirectCallService livekit=mock(DirectCallService.class);
  private final PersonalLiveInteractionService service=
    new PersonalLiveInteractionService(lives,chat,guests,mutes,profiles,blocks,livekit);
  private Profile owner,viewer;
  private PersonalLiveEvent event;

  @BeforeEach void init() {
    owner=new Profile();owner.setId(1L);owner.setUsername("organizer");
    viewer=new Profile();viewer.setId(2L);viewer.setUsername("viewer");
    event=PersonalLiveEvent.builder().id(15L).host(owner).status(PersonalLiveEvent.Status.LIVE)
      .chatEnabled(true).title("Jogo").createdAt(Instant.now()).build();
    when(lives.findById(15L)).thenReturn(Optional.of(event));
    when(profiles.findUserByUsernameIgnoreCase("organizer")).thenReturn(Optional.of(owner));
    when(profiles.findUserByUsernameIgnoreCase("viewer")).thenReturn(Optional.of(viewer));
    when(chat.save(any())).thenAnswer(i->{
      PersonalLiveChatMessage m=i.getArgument(0);
      m.setId(44L);return m;
    });
  }
  @Test void spectatorsMaySendChatMessagesButMutedUsersCannot() {
    assertEquals("Bem jogado!",service.send("viewer",15L," Bem jogado! ").message());
    verify(chat).save(any());
    when(mutes.existsByLiveIdAndProfileId(15L,2L)).thenReturn(true);
    assertThrows(ResponseStatusException.class,()->service.send("viewer",15L,"teste"));
  }
  @Test void onlyHostCanMuteUsersOrDisableChat() {
    assertThrows(ResponseStatusException.class,()->service.toggleChat("viewer",15L,false));
    assertThrows(ResponseStatusException.class,()->service.mute("viewer",15L,"organizer",true));
    service.toggleChat("organizer",15L,false);
    assertFalse(event.isChatEnabled());
    assertThrows(ResponseStatusException.class,()->service.send("viewer",15L,"teste"));
    assertDoesNotThrow(()->service.send("organizer",15L,"mensagem de organizador"));
  }
  @Test void requestsDoNotGrantPublishingUntilHostApproval() {
    when(guests.save(any())).thenAnswer(i->{
      PersonalLiveGuestRequest request=i.getArgument(0);request.setId(30L);return request;
    });
    assertEquals("PENDING",service.askToJoin("viewer",15L).status());
    assertNull(event.getGuest());
    PersonalLiveGuestRequest pending=PersonalLiveGuestRequest.builder().id(30L)
      .live(event).requester(viewer).status(PersonalLiveGuestRequest.Status.PENDING).build();
    when(guests.findById(30L)).thenReturn(Optional.of(pending));
    assertThrows(ResponseStatusException.class,
      ()->service.decide("viewer",15L,30L,true));
    assertEquals("APPROVED",service.decide("organizer",15L,30L,true).status());
    assertEquals(viewer.getId(),event.getGuest().getId());
  }
}
