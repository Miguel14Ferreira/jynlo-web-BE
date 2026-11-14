package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.social.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.social.*;
import lombok.RequiredArgsConstructor;
import com.example.desporto24.websocket.ChatRealtimeGateway;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PersonalLiveInteractionService {
  private final PersonalLiveEventRepository events;
  private final PersonalLiveChatMessageRepository messages;
  private final PersonalLiveGuestRequestRepository requests;
  private final PersonalLiveMuteRepository mutes;
  private final ProfileRepository profiles;
  private final UserBlockService blocks;
  private final DirectCallService provider;
  private final ChatRealtimeGateway realtime;

  private void notifyLiveChanged() {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override public void afterCommit() { realtime.publishPersonalLiveInvalidated(); }
      });
    } else realtime.publishPersonalLiveInvalidated();
  }

  public record ChatMessage(long id,String username,String message,Instant createdAt) {}
  public record GuestRequest(long id,String username,String status,Instant createdAt) {}
  public record LiveControls(boolean chatEnabled,boolean mutedByHost,String guestUsername,
                             boolean isGuest,String requestStatus,List<GuestRequest> pendingRequests,
                             List<String> mutedUsernames) {}

  private Profile actor(String username) {
    return profiles.findUserByUsernameIgnoreCase(username)
      .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }
  private PersonalLiveEvent visible(long id,Profile viewer) {
    PersonalLiveEvent event=events.findById(id)
      .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Direto indisponível"));
    if(event.getStatus()!=PersonalLiveEvent.Status.LIVE ||
        blocks.isBlockedBetween(viewer,event.getHost()))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Direto indisponível");
    return event;
  }
  private void owner(PersonalLiveEvent e,Profile user) {
    if(!e.getHost().getId().equals(user.getId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Apenas o apresentador pode moderar");
  }

  @Transactional(readOnly=true)
  public List<ChatMessage> chat(String username,long liveId) {
    Profile viewer=actor(username);
    PersonalLiveEvent event=visible(liveId,viewer);
    return messages.findTop100ByLiveIdOrderByCreatedAtDesc(liveId).stream()
      .filter(msg->!blocks.isBlockedBetween(viewer,msg.getAuthor()))
      .sorted(Comparator.comparing(PersonalLiveChatMessage::getCreatedAt)
         .thenComparing(PersonalLiveChatMessage::getId))
      .map(msg->new ChatMessage(msg.getId(),msg.getAuthor().getUsername(),
        msg.getMessage(),msg.getCreatedAt())).toList();
  }
  @Transactional
  public ChatMessage send(String username,long liveId,String value) {
    Profile author=actor(username);
    PersonalLiveEvent event=visible(liveId,author);
    if(value==null||value.isBlank()||value.strip().length()>500)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mensagem inválida");
    if(!event.isChatEnabled() && !event.getHost().getId().equals(author.getId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Chat desativado");
    if(!event.getHost().getId().equals(author.getId()) &&
        mutes.existsByLiveIdAndProfileId(liveId,author.getId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Não podes escrever neste direto");
    PersonalLiveChatMessage msg=messages.save(PersonalLiveChatMessage.builder().live(event)
      .author(author).message(value.strip()).createdAt(Instant.now()).build());
    notifyLiveChanged();
    return new ChatMessage(msg.getId(),username,msg.getMessage(),msg.getCreatedAt());
  }
  @Transactional(readOnly=true)
  public LiveControls controls(String username,long liveId) {
    Profile viewer=actor(username);
    PersonalLiveEvent event=visible(liveId,viewer);
    boolean host=event.getHost().getId().equals(viewer.getId());
    String requested=requests.findByLiveIdAndRequesterId(liveId,viewer.getId())
      .map(req->req.getStatus().name()).orElse(null);
    List<GuestRequest> pending=host? requests.findByLiveIdAndStatusOrderByCreatedAtAsc(
      liveId,PersonalLiveGuestRequest.Status.PENDING).stream().map(this::view).toList():List.of();
    return new LiveControls(event.isChatEnabled(),
       mutes.existsByLiveIdAndProfileId(liveId,viewer.getId()),
       event.getGuest()!=null?event.getGuest().getUsername():null,
       event.getGuest()!=null && event.getGuest().getId().equals(viewer.getId()),
       requested,pending,host?mutes.findByLiveId(liveId).stream()
         .map(m->m.getProfile().getUsername()).toList():List.of());
  }
  private GuestRequest view(PersonalLiveGuestRequest req) {
    return new GuestRequest(req.getId(),req.getRequester().getUsername(),
      req.getStatus().name(),req.getCreatedAt());
  }
  @Transactional
  public LiveControls toggleChat(String username,long liveId,boolean enabled) {
    Profile user=actor(username);PersonalLiveEvent event=visible(liveId,user);
    owner(event,user);event.setChatEnabled(enabled);events.save(event);
    notifyLiveChanged();
    return controls(username,liveId);
  }
  @Transactional
  public LiveControls mute(String username,long liveId,String targetName,boolean muted) {
    Profile owner=actor(username);PersonalLiveEvent event=visible(liveId,owner);
    owner(event,owner);
    Profile target=actor(targetName);
    if(target.getId().equals(owner.getId()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Não podes silenciar-te");
    if(muted && !mutes.existsByLiveIdAndProfileId(liveId,target.getId()))
      mutes.save(PersonalLiveMute.builder().live(event).profile(target)
        .createdAt(Instant.now()).build());
    if(!muted)mutes.deleteByLiveIdAndProfileId(liveId,target.getId());
    notifyLiveChanged();
    return controls(username,liveId);
  }
  @Transactional
  public GuestRequest askToJoin(String username,long liveId) {
    Profile viewer=actor(username);PersonalLiveEvent event=visible(liveId,viewer);
    if(viewer.getId().equals(event.getHost().getId()))
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Já és apresentador");
    PersonalLiveGuestRequest req=requests.findByLiveIdAndRequesterId(liveId,viewer.getId())
      .orElse(null);
    if(req==null)req=PersonalLiveGuestRequest.builder().live(event)
      .requester(viewer).createdAt(Instant.now()).build();
    if(req.getStatus()!=PersonalLiveGuestRequest.Status.APPROVED) {
      req.setStatus(PersonalLiveGuestRequest.Status.PENDING);
      req.setCreatedAt(Instant.now());
    }
    GuestRequest saved=view(requests.save(req));
    notifyLiveChanged();
    return saved;
  }
  @Transactional
  public GuestRequest decide(String username,long liveId,long requestId,boolean approve) {
    Profile host=actor(username);PersonalLiveEvent event=visible(liveId,host);owner(event,host);
    PersonalLiveGuestRequest req=requests.findById(requestId)
      .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido indisponível"));
    if(!req.getLive().getId().equals(liveId)||
        req.getStatus()!=PersonalLiveGuestRequest.Status.PENDING)
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Pedido já resolvido");
    if(approve && event.getGuest()!=null &&
        !event.getGuest().getId().equals(req.getRequester().getId()))
      throw new ResponseStatusException(HttpStatus.CONFLICT,"Já há um convidado em direto");
    if(approve) {
      blocks.ensureInteractionAllowed(host,req.getRequester());
      event.setGuest(req.getRequester());events.save(event);
    }
    req.setStatus(approve?PersonalLiveGuestRequest.Status.APPROVED:
      PersonalLiveGuestRequest.Status.DECLINED);
    GuestRequest saved=view(requests.save(req));
    notifyLiveChanged();
    return saved;
  }
  @Transactional
  public void removeGuest(String username,long liveId) {
    Profile host=actor(username);PersonalLiveEvent event=visible(liveId,host);owner(event,host);
    Profile guest=event.getGuest();
    if(guest==null)return;
    // Revoke existing LiveKit publish access before removing the grant.
    try {
      if(!provider.removeFromLiveKit("jynlo-live-personal-"+liveId,
          "jynlo-live-personal-"+liveId+"-user-"+guest.getId()))
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "Não foi possível retirar o convidado da sala");
    } catch(ResponseStatusException e){throw e;}
      catch(Exception e){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "Não foi possível retirar o convidado da sala");}
    event.setGuest(null);events.save(event);
    requests.findByLiveIdAndRequesterId(liveId,guest.getId()).ifPresent(req->{
      req.setStatus(PersonalLiveGuestRequest.Status.DECLINED);requests.save(req);
    });
    notifyLiveChanged();
  }
}
