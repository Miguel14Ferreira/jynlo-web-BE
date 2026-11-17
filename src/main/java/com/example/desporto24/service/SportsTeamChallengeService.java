package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.*;
import com.example.desporto24.service.interfaces.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

/**
 * Challenges never invite people to a session automatically. The captains
 * agree first and explicitly link a separately created public session.
 */
@Service
@RequiredArgsConstructor
public class SportsTeamChallengeService {
    private final SportsTeamRepository teams;
    private final SportsTeamChallengeRepository challenges;
    private final ProfileRepository profiles;
    private final SessionService sessions;

    public record ChallengeView(Long id, Long challengerTeamId, String challengerTeamName,
            Long challengedTeamId, String challengedTeamName, String status,
            String message, Long sessionId, Instant createdAt, Instant respondedAt) {}

    private Profile actor(String username) {
        Profile profile=profiles.findUserByUsername(username)
                .orElseThrow(()->new AccessDeniedException("Inicia sessão para gerir desafios"));
        if(Boolean.FALSE.equals(profile.getEnabled()) || profile.getModerationSuspendedAt()!=null)
            throw new AccessDeniedException("A tua conta não pode gerir desafios");
        return profile;
    }

    private SportsTeam required(Long id) {
        if(id==null || id<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Equipa inválida");
        return teams.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa não encontrada"));
    }

    private ChallengeView view(SportsTeamChallenge c) {
        return new ChallengeView(c.getId(),c.getChallenger().getId(),c.getChallenger().getName(),
                c.getChallenged().getId(),c.getChallenged().getName(),
                c.getStatus().name(),c.getMessage(),c.getSessionId(),c.getCreatedAt(),c.getRespondedAt());
    }

    @Transactional(readOnly=true)
    public List<ChallengeView> forTeam(Long teamId,String username) {
        // Team challenge cards are public, but user authentication is required
        // so the same response can support owner-only actions in the UI.
        actor(username);
        required(teamId);
        return challenges.forTeam(teamId,PageRequest.of(0,30)).stream().map(this::view).toList();
    }

    @Transactional
    public ChallengeView create(Long challengerId,Long challengedId,String message,String username) {
        Profile requester=actor(username);
        SportsTeam challenger=required(challengerId);
        SportsTeam challenged=required(challengedId);
        if(!challenger.getOwner().getId().equals(requester.getId()))
            throw new AccessDeniedException("Só o responsável pode desafiar em nome da equipa");
        if(challenger.getId().equals(challenged.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Não podes desafiar a tua própria equipa");
        if(challenged.getOwner().getId().equals(requester.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Estas duas equipas têm o mesmo responsável");
        if(challenger.getSport()!=null && challenged.getSport()!=null
                && !challenger.getSport().isBlank() && !challenged.getSport().isBlank()
                && !challenger.getSport().trim().equalsIgnoreCase(challenged.getSport().trim()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"As equipas praticam modalidades diferentes");
        if(message!=null && message.trim().length()>280)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mensagem demasiado longa");
        if(challenges.hasActivePair(challengerId,challengedId,
                List.of(SportsTeamChallenge.Status.PENDING,SportsTeamChallenge.Status.ACCEPTED)))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe um desafio pendente ou aceite entre estas equipas");
        var c=SportsTeamChallenge.builder().challenger(challenger).challenged(challenged)
                .createdBy(requester).status(SportsTeamChallenge.Status.PENDING)
                .message(message==null?null:message.trim()).createdAt(Instant.now()).build();
        return view(challenges.saveAndFlush(c));
    }

    @Transactional
    public ChallengeView respond(Long id,boolean accept,String username) {
        Profile profile=actor(username);
        var c=challenges.findWithTeams(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Desafio não encontrado"));
        if(!c.getChallenged().getOwner().getId().equals(profile.getId()))
            throw new AccessDeniedException("Só o responsável da equipa desafiada pode responder");
        if(c.getStatus()!=SportsTeamChallenge.Status.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Este desafio já foi respondido");
        c.setStatus(accept?SportsTeamChallenge.Status.ACCEPTED:SportsTeamChallenge.Status.DECLINED);
        c.setRespondedAt(Instant.now());
        return view(challenges.saveAndFlush(c));
    }

    @Transactional
    public ChallengeView cancel(Long id,String username) {
        Profile profile=actor(username);
        var c=challenges.findWithTeams(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Desafio não encontrado"));
        if(!c.getChallenger().getOwner().getId().equals(profile.getId()))
            throw new AccessDeniedException("Só o responsável que lançou o desafio pode cancelar");
        if(c.getStatus()!=SportsTeamChallenge.Status.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Só os desafios pendentes podem ser cancelados");
        c.setStatus(SportsTeamChallenge.Status.CANCELLED);
        c.setRespondedAt(Instant.now());
        return view(challenges.saveAndFlush(c));
    }

    @Transactional
    public ChallengeView linkSession(Long id,Long sessionId,String username) {
        Profile profile=actor(username);
        var c=challenges.findWithTeams(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Desafio não encontrado"));
        if(c.getStatus()!=SportsTeamChallenge.Status.ACCEPTED || c.getSessionId()!=null)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"O desafio não está disponível para associar um jogo");
        boolean captain=c.getChallenger().getOwner().getId().equals(profile.getId())
                ||c.getChallenged().getOwner().getId().equals(profile.getId());
        if(!captain)throw new AccessDeniedException("Só os responsáveis podem associar uma sessão");
        if(sessionId==null||sessionId<=0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Sessão inválida");
        var session=sessions.getById(sessionId,username);
        // A public session with an authorised captain as its creator is required.
        // Never store a private session link in a public team-challenge record.
        if(!session.canSharePublicly() || session.privateGame()
                || session.creatorUsername()==null
                || !session.creatorUsername().equalsIgnoreCase(username))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Só podes associar uma sessão pública criada por ti");
        if(session.sport()!=null) {
            String sport=session.sport().trim().toLowerCase(Locale.ROOT);
            for(var team:List.of(c.getChallenger(),c.getChallenged())) {
                if(team.getSport()!=null&&!team.getSport().isBlank()
                        && !team.getSport().trim().toLowerCase(Locale.ROOT).equals(sport))
                    throw new ResponseStatusException(HttpStatus.CONFLICT,"Modalidade incompatível com a sessão");
            }
        }
        c.setSessionId(sessionId);
        c.setLinkedAt(Instant.now());
        return view(challenges.saveAndFlush(c));
    }
}
