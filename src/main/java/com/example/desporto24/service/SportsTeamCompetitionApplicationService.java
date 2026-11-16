package com.example.desporto24.service;

import com.example.desporto24.enumeration.*;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import com.example.desporto24.repository.team.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SportsTeamCompetitionApplicationService {
    private final SportsTeamRepository teams;
    private final SportsTeamCompetitionApplicationRepository applications;
    private final ProfileRepository profiles;
    private final CommunityMemberRepository members;
    private final CommunityTournamentRepository tournaments;
    private final CommunityChampionshipRepository championships;

    public record CompetitionView(Long id, Long communityId, String communityName,
        String type, String name, String sport, boolean applicationsOpen) {}
    public record ApplicationView(Long id, Long teamId, String teamName, Long communityId,
        String competitionType, Long competitionId, String status) {}

    private ApplicationView view(SportsTeamCompetitionApplication app) {
        return new ApplicationView(app.getId(),app.getTeam().getId(),app.getTeam().getName(),
            app.getCommunityId(),app.getCompetitionType().name(),app.getCompetitionId(),app.getStatus().name());
    }
    private String filter(String q) {
        return q == null ? "" : q.trim().substring(0,Math.min(80,q.trim().length()));
    }
    @Transactional(readOnly = true)
    public List<CompetitionView> discover(String query, String sport) {
        String q = filter(query), s = filter(sport);
        List<CompetitionView> result = new ArrayList<>();
        for(var t : tournaments.discoverOpenTeams(q,s,PageRequest.of(0,40))) {
            result.add(new CompetitionView(t.getId(),t.getCommunity().getId(),
                t.getCommunity().getName(),"TOURNAMENT",t.getName(),t.getSport(),true));
        }
        for(var c : championships.discoverOpenTeams(q,s,PageRequest.of(0,40))) {
            result.add(new CompetitionView(c.getId(),c.getCommunity().getId(),
                c.getCommunity().getName(),"CHAMPIONSHIP",c.getName(),c.getSport(),true));
        }
        return result.stream().limit(60).toList();
    }
    @Transactional(readOnly = true)
    public List<ApplicationView> mine(String username) {
        var actor = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        return applications.findByTeamOwnerIdOrderByIdDesc(actor.getId()).stream()
            .map(this::view).toList();
    }
    @Transactional(readOnly = true)
    public List<ApplicationView> forCompetition(Long communityId, String type, Long competitionId, String username) {
        var actor = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireOrganizer(communityId, actor.getId());
        var parsed = parseType(type);
        getCompetition(communityId,parsed,competitionId);
        return applications.findByCommunityIdAndCompetitionTypeAndCompetitionIdOrderByIdAsc(
            communityId,parsed,competitionId).stream().map(this::view).toList();
    }
    @Transactional(readOnly = true)
    public CompetitionView settings(Long communityId,String type,Long competitionId,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireOrganizer(communityId,actor.getId());
        var parsed=parseType(type);
        if(parsed==SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT) {
            var t=tournaments.findById(competitionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if(!t.getCommunity().getId().equals(communityId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return new CompetitionView(t.getId(),communityId,t.getCommunity().getName(),
                "TOURNAMENT",t.getName(),t.getSport(),t.isTeamApplicationsOpen());
        }
        var championship=championships.findByIdAndCommunityId(competitionId,communityId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new CompetitionView(championship.getId(),communityId,
            championship.getCommunity().getName(),"CHAMPIONSHIP",championship.getName(),
            championship.getSport(),championship.isTeamApplicationsOpen());
    }

    @Transactional
    public CompetitionView configure(Long communityId,String type,Long competitionId,boolean open,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireEligibleActor(actor);
        requireOrganizer(communityId,actor.getId());
        var parsed=parseType(type);
        if(parsed==SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT) {
            CommunityTournament t=tournaments.findByIdForUpdate(competitionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if(!t.getCommunity().getId().equals(communityId))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            if(open && (t.getEntryType()!=TournamentEntryType.TEAM||t.getStatus()!=TournamentStatus.DRAFT))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Apenas torneios por equipas em preparação");
            if(open && t.getCommunity().getVisibility()!=CommunityVisibility.PUBLIC)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Torna a comunidade pública para receber pedidos globais");
            t.setTeamApplicationsOpen(open);
            tournaments.saveAndFlush(t);
            return new CompetitionView(t.getId(),communityId,t.getCommunity().getName(),"TOURNAMENT",
                t.getName(),t.getSport(),open);
        }
        CommunityChampionship c=championships.findForClosing(competitionId,communityId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(open && c.getClosedAt()!=null)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Campeonato encerrado");
        if(open && c.getCommunity().getVisibility()!=CommunityVisibility.PUBLIC)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Torna a comunidade pública para receber pedidos globais");
        c.setTeamApplicationsOpen(open);
        championships.saveAndFlush(c);
        return new CompetitionView(c.getId(),communityId,c.getCommunity().getName(),"CHAMPIONSHIP",
            c.getName(),c.getSport(),open);
    }
    @Transactional
    public ApplicationView apply(Long communityId,String type,Long competitionId,Long teamId,String username) {
        var actor = profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireEligibleActor(actor);
        SportsTeam team=teams.findById(teamId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa não encontrada"));
        if(!team.getOwner().getId().equals(actor.getId()))
            throw new AccessDeniedException("Só o responsável da equipa pode candidatar-se");
        var parsed=parseType(type);
        ensureAccepting(communityId,parsed,competitionId);
        if(applications.findByTeamIdAndCompetitionTypeAndCompetitionId(teamId,parsed,competitionId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe candidatura desta equipa");
        var application=SportsTeamCompetitionApplication.builder()
            .team(team).requestedBy(actor).communityId(communityId).competitionType(parsed)
            .competitionId(competitionId).status(SportsTeamCompetitionApplication.Status.PENDING)
            .createdAt(Instant.now()).build();
        return view(applications.saveAndFlush(application));
    }
    @Transactional
    public ApplicationView decide(Long id,boolean approve,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireEligibleActor(actor);
        var application=applications.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Candidatura não encontrada"));
        requireOrganizer(application.getCommunityId(),actor.getId());
        if(application.getStatus()!=SportsTeamCompetitionApplication.Status.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Pedido já respondido");
        if(approve)ensureAccepting(application.getCommunityId(),application.getCompetitionType(),
            application.getCompetitionId());
        application.setStatus(approve?SportsTeamCompetitionApplication.Status.APPROVED:
            SportsTeamCompetitionApplication.Status.DECLINED);
        application.setRespondedAt(Instant.now());
        // Approval means consent by organizer. Competition team entry and players must be
        // finalized through the existing competition eligibility/roster workflow.
        return view(applications.saveAndFlush(application));
    }
    private SportsTeamCompetitionApplication.CompetitionType parseType(String type) {
        try{return SportsTeamCompetitionApplication.CompetitionType.valueOf(type.toUpperCase(Locale.ROOT));}
        catch(Exception ex){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tipo inválido");}
    }
    private void requireEligibleActor(com.example.desporto24.model.Profile actor) {
        // A valid but stale JWT must not authorize competition changes after
        // the user's account has been disabled or moderation-suspended.
        if (Boolean.FALSE.equals(actor.getEnabled()) || actor.getModerationSuspendedAt() != null)
            throw new AccessDeniedException("Conta indisponível para gerir competições");
    }

    private void requireOrganizer(Long cid,Long pid) {
        var membership=members.findByCommunityIdAndProfileId(cid,pid)
            .orElseThrow(()->new AccessDeniedException("Não pertences à comunidade"));
        if(membership.getRole()!=CommunityRole.OWNER && membership.getRole()!=CommunityRole.ADMIN)
            throw new AccessDeniedException("Só organizadores podem gerir candidaturas");
    }
    private void getCompetition(Long cid,SportsTeamCompetitionApplication.CompetitionType type,Long id) {
        if(type==SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT) {
            var t=tournaments.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            if(!t.getCommunity().getId().equals(cid))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            championships.findByIdAndCommunityId(id,cid)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
    }
    private void ensureAccepting(Long cid,SportsTeamCompetitionApplication.CompetitionType type,Long id) {
        if(type==SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT) {
            var t=tournaments.findByIdForUpdate(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            if(!t.getCommunity().getId().equals(cid))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            if(t.getCommunity().getVisibility()!=CommunityVisibility.PUBLIC||!t.isTeamApplicationsOpen()
                ||t.getStatus()!=TournamentStatus.DRAFT||t.getEntryType()!=TournamentEntryType.TEAM)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Este torneio não aceita candidaturas");
        } else {
            var c=championships.findForClosing(id,cid)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            if(c.getCommunity().getVisibility()!=CommunityVisibility.PUBLIC||!c.isTeamApplicationsOpen()
                ||c.getClosedAt()!=null)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Este campeonato não aceita candidaturas");
        }
    }
}
