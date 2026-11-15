package com.example.desporto24.service;

import com.example.desporto24.enumeration.*;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.*;
import com.example.desporto24.model.team.*;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.*;
import com.example.desporto24.repository.team.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

/**
 * Explicit organizer-controlled finalization of accepted invitations or approved applications.
 * Reuses the global team identity. Never registers players implicitly or changes existing fixtures.
 */
@Service
@RequiredArgsConstructor
public class SportsTeamCompetitionEnrollmentService {
    private static final int MAX_TOURNAMENT_TEAMS=32;
    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository globalMembers;
    private final SportsTeamCompetitionInvitationRepository invitations;
    private final SportsTeamCompetitionApplicationRepository applications;
    private final CommunityTournamentRepository tournaments;
    private final CommunityChampionshipRepository championships;
    private final CommunityTournamentTeamRepository tournamentTeams;
    private final CommunityChampionshipTeamRepository championshipTeams;
    private final CommunityMemberRepository communityMembers;
    private final ProfileRepository profiles;
    private final CommunityTournamentTeamService tournamentRosters;
    private final CommunityChampionshipTeamsService championshipRosters;

    public record EntryView(Long id,Long globalTeamId,Long communityId,Long competitionId,
                            String competitionType,String name,int confirmedGlobalPlayers,
                            String message) {}
    public record RosterCandidate(Long profileId,String username,boolean communityMember) {}

    @Transactional(readOnly=true)
    public EntryView existingEntry(Long communityId,String type,Long competitionId,
                                   Long globalTeamId,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(()->new AccessDeniedException("Utilizador desconhecido"));
        var role=communityMembers.findByCommunityIdAndProfileId(communityId,actor.getId())
            .map(CommunityMember::getRole).orElse(null);
        if(role!=CommunityRole.ADMIN&&role!=CommunityRole.OWNER)
            throw new AccessDeniedException("Só o organizador pode consultar inscrições");
        var team=teams.findById(globalTeamId)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa inexistente"));
        String kind=type==null?"":type.toUpperCase(Locale.ROOT);
        int confirmed=1+globalMembers.findByTeamIdAndStatusOrderByIdAsc(globalTeamId,
            SportsTeamMember.Status.ACTIVE).size();
        if("TOURNAMENT".equals(kind)) {
            var tournament=tournaments.findById(competitionId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Torneio inexistente"));
            if(!communityId.equals(tournament.getCommunity().getId()))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Torneio inexistente");
            var entry=tournamentTeams.findByTournamentIdOrderByIdAsc(competitionId).stream()
                .filter(t->t.getGlobalTeam()!=null&&globalTeamId.equals(t.getGlobalTeam().getId()))
                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa ainda não inscrita"));
            return new EntryView(entry.getId(),team.getId(),communityId,competitionId,kind,entry.getName(),
                confirmed,"Equipa já inscrita. Os jogadores continuam a precisar de confirmar a participação.");
        }
        if("CHAMPIONSHIP".equals(kind)) {
            championships.findByIdAndCommunityId(competitionId,communityId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Campeonato inexistente"));
            var entry=championshipTeams.findByChampionshipIdOrderByNameAscIdAsc(competitionId).stream()
                .filter(t->t.getGlobalTeam()!=null&&globalTeamId.equals(t.getGlobalTeam().getId()))
                .findFirst().orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa ainda não inscrita"));
            return new EntryView(entry.getId(),team.getId(),communityId,competitionId,kind,entry.getName(),
                confirmed,"Equipa já inscrita. Seleciona jogadores para convidar.");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tipo de competição inválido");
    }

    @Transactional
    public EntryView finalizeEntry(Long communityId,String type,Long competitionId,
                                   Long globalTeamId,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(()->new AccessDeniedException("Utilizador desconhecido"));
        var role=communityMembers.findByCommunityIdAndProfileId(communityId,actor.getId())
            .map(CommunityMember::getRole).orElse(null);
        if(role!=CommunityRole.ADMIN&&role!=CommunityRole.OWNER)
            throw new AccessDeniedException("Só os administradores podem finalizar inscrições");

        var team=teams.findById(globalTeamId)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa não encontrada"));
        String kind=type==null?"":type.toUpperCase(Locale.ROOT);
        String normalized=team.getName().trim().toLowerCase(Locale.ROOT);
        int confirmed=1+globalMembers.findByTeamIdAndStatusOrderByIdAsc(globalTeamId,
            SportsTeamMember.Status.ACTIVE).size();

        if("TOURNAMENT".equals(kind)) {
            var tournament=tournaments.findByIdForUpdate(competitionId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Torneio não encontrado"));
            if(!tournament.getCommunity().getId().equals(communityId))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Torneio não encontrado");
            if(tournament.getEntryType()!=TournamentEntryType.TEAM||
               tournament.getStatus()!=TournamentStatus.DRAFT||tournament.getHistoricalArchiveId()!=null)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"O torneio não aceita novas equipas");
            requireConsent(globalTeamId,communityId,kind,competitionId);
            if(tournamentTeams.existsByTournamentIdAndGlobalTeamId(competitionId,globalTeamId))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Esta equipa já está inscrita");
            if(tournamentTeams.existsByTournamentIdAndNormalizedName(competitionId,normalized))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe uma equipa com este nome no torneio");
            if(tournamentTeams.countByTournamentId(competitionId)>=MAX_TOURNAMENT_TEAMS)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"O torneio atingiu o limite de equipas");
            // The global captain remains the identity owner. Competition permissions
            // remain restricted to actual community members under existing team rules.
            Profile eligibleCaptain=communityMembers.existsByCommunityIdAndProfileId(communityId,
                team.getOwner().getId())?team.getOwner():null;
            var created=tournamentTeams.saveAndFlush(CommunityTournamentTeam.builder()
                .tournament(tournament).globalTeam(team).name(team.getName())
                .normalizedName(normalized).adminProfile(eligibleCaptain)
                .createdAt(Instant.now()).build());
            return new EntryView(created.getId(),globalTeamId,communityId,competitionId,
                kind,created.getName(),confirmed,
                "Equipa inscrita. Convida os jogadores elegíveis; cada um confirma a sua participação.");
        }
        if("CHAMPIONSHIP".equals(kind)) {
            var championship=championships.findForClosing(competitionId,communityId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Campeonato não encontrado"));
            if(championship.getClosedAt()!=null)
                throw new ResponseStatusException(HttpStatus.CONFLICT,"O campeonato está encerrado");
            requireConsent(globalTeamId,communityId,kind,competitionId);
            if(championshipTeams.existsByChampionshipIdAndGlobalTeamId(competitionId,globalTeamId))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Esta equipa já está inscrita");
            if(championshipTeams.existsByChampionshipIdAndNormalizedName(competitionId,normalized))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe uma equipa com este nome no campeonato");
            Profile eligibleCaptain=communityMembers.existsByCommunityIdAndProfileId(communityId,
                team.getOwner().getId())?team.getOwner():null;
            var created=championshipTeams.saveAndFlush(CommunityChampionshipTeam.builder()
                .championship(championship).globalTeam(team).name(team.getName())
                .normalizedName(normalized).adminProfile(eligibleCaptain)
                .createdAt(Instant.now()).build());
            return new EntryView(created.getId(),globalTeamId,communityId,competitionId,
                kind,created.getName(),confirmed,
                "Equipa inscrita. Seleciona os jogadores da equipa e envia convites individuais.");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tipo de competição inválido");
    }

    /** The confirmed global roster is offered as candidates, never enrolled automatically.
     * Organizer access requires a recorded accepted invite or approved application. */
    @Transactional(readOnly=true)
    public List<RosterCandidate> globalRoster(Long communityId,String type,Long competitionId,
                                               Long globalTeamId,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(()->new AccessDeniedException("Utilizador desconhecido"));
        var team=teams.findById(globalTeamId)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa não encontrada"));
        var role=communityMembers.findByCommunityIdAndProfileId(communityId,actor.getId())
            .map(CommunityMember::getRole).orElse(null);
        if(!team.getOwner().getId().equals(actor.getId())
           &&role!=CommunityRole.OWNER&&role!=CommunityRole.ADMIN)
            throw new AccessDeniedException("Só o capitão ou organizador pode consultar o plantel");
        requireConsent(globalTeamId,communityId,type.toUpperCase(Locale.ROOT),competitionId);
        var players=new java.util.ArrayList<RosterCandidate>();
        var owner=team.getOwner();
        players.add(new RosterCandidate(owner.getId(),owner.getUsername(),
            communityMembers.existsByCommunityIdAndProfileId(communityId,owner.getId())));
        for(var member:globalMembers.findByTeamIdAndStatusOrderByIdAsc(globalTeamId,
            SportsTeamMember.Status.ACTIVE)) {
            var p=member.getProfile();
            if(!p.getId().equals(owner.getId()))
                players.add(new RosterCandidate(p.getId(),p.getUsername(),
                    communityMembers.existsByCommunityIdAndProfileId(communityId,p.getId())));
        }
        return players;
    }

    /**
     * The organizer selects already confirmed global team members. Existing competition
     * services send individual invitations (player acceptance is still required).
     * Entire selection is atomic: one ineligible player rolls back the batch.
     */
    @Transactional
    public int inviteSelected(Long communityId,String type,Long competitionId,
                              Long globalTeamId,List<Long> profileIds,String username) {
        var actor=profiles.findUserByUsername(username)
            .orElseThrow(()->new AccessDeniedException("Utilizador desconhecido"));
        var role=communityMembers.findByCommunityIdAndProfileId(communityId,actor.getId())
            .map(CommunityMember::getRole).orElse(null);
        if(role!=CommunityRole.OWNER&&role!=CommunityRole.ADMIN)
            throw new AccessDeniedException("Só o organizador pode convidar os jogadores");
        if(profileIds==null||profileIds.isEmpty()||profileIds.size()>30
           ||profileIds.stream().anyMatch(id->id==null||id<=0)
           ||profileIds.stream().distinct().count()!=profileIds.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Seleciona até 30 jogadores diferentes");
        String kind=type==null?"":type.toUpperCase(Locale.ROOT);
        requireConsent(globalTeamId,communityId,kind,competitionId);
        var globalTeam=teams.findById(globalTeamId)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa inexistente"));
        java.util.Set<Long> accepted=new java.util.HashSet<>();
        accepted.add(globalTeam.getOwner().getId());
        for(var m:globalMembers.findByTeamIdAndStatusOrderByIdAsc(globalTeamId,
            SportsTeamMember.Status.ACTIVE)) accepted.add(m.getProfile().getId());
        for(Long id:profileIds) {
            if(!accepted.contains(id)||
               !communityMembers.existsByCommunityIdAndProfileId(communityId,id))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Todos os jogadores escolhidos devem pertencer à equipa e à comunidade");
        }
        if("TOURNAMENT".equals(kind)) {
            var registered=tournamentTeams.findByTournamentIdOrderByIdAsc(competitionId).stream()
                .filter(t->t.getGlobalTeam()!=null
                   &&globalTeamId.equals(t.getGlobalTeam().getId())).findFirst()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Finaliza primeiro a inscrição da equipa no torneio"));
            for(Long id:profileIds)
                tournamentRosters.invitePlayer(communityId,competitionId,registered.getId(),username,id);
        } else if("CHAMPIONSHIP".equals(kind)) {
            var registered=championshipTeams.findByChampionshipIdOrderByNameAscIdAsc(competitionId)
                .stream().filter(t->t.getGlobalTeam()!=null
                    &&globalTeamId.equals(t.getGlobalTeam().getId())).findFirst()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Finaliza primeiro a inscrição da equipa no campeonato"));
            for(Long id:profileIds)
                championshipRosters.invitePlayer(communityId,competitionId,registered.getId(),id,username);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tipo de competição inválido");
        }
        return profileIds.size();
    }

    private void requireConsent(Long globalTeamId,Long communityId,String type,Long competitionId) {
        if("TOURNAMENT".equals(type)) {
            var invite=invitations.findByTeamIdAndCompetitionTypeAndCompetitionId(globalTeamId,
                SportsTeamCompetitionInvitation.CompetitionType.TOURNAMENT,competitionId);
            var application=applications.findByTeamIdAndCompetitionTypeAndCompetitionId(globalTeamId,
                SportsTeamCompetitionApplication.CompetitionType.TOURNAMENT,competitionId);
            if(invite.filter(i->i.getCommunityId().equals(communityId)
                &&i.getStatus()==SportsTeamCompetitionInvitation.Status.ACCEPTED).isPresent()
                ||application.filter(a->a.getCommunityId().equals(communityId)
                &&a.getStatus()==SportsTeamCompetitionApplication.Status.APPROVED).isPresent())return;
        } else {
            var invite=invitations.findByTeamIdAndCompetitionTypeAndCompetitionId(globalTeamId,
                SportsTeamCompetitionInvitation.CompetitionType.CHAMPIONSHIP,competitionId);
            var application=applications.findByTeamIdAndCompetitionTypeAndCompetitionId(globalTeamId,
                SportsTeamCompetitionApplication.CompetitionType.CHAMPIONSHIP,competitionId);
            if(invite.filter(i->i.getCommunityId().equals(communityId)
                &&i.getStatus()==SportsTeamCompetitionInvitation.Status.ACCEPTED).isPresent()
                ||application.filter(a->a.getCommunityId().equals(communityId)
                &&a.getStatus()==SportsTeamCompetitionApplication.Status.APPROVED).isPresent())return;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "É necessário um convite aceite ou uma candidatura aprovada antes de inscrever a equipa");
    }
}
