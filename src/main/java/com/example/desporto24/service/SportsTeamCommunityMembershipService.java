package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityRole;
import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.community.Community;
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

@Service
@RequiredArgsConstructor
public class SportsTeamCommunityMembershipService {
    private final SportsTeamCommunityMembershipRepository memberships;
    private final SportsTeamRepository teams;
    private final CommunityRepository communities;
    private final CommunityMemberRepository communityMembers;
    private final ProfileRepository profiles;

    public record MembershipView(Long id,Long teamId,String teamName,Long communityId,
          String communityName,String status) {}

    private MembershipView view(SportsTeamCommunityMembership m) {
        return new MembershipView(m.getId(),m.getTeam().getId(),m.getTeam().getName(),
            m.getCommunity().getId(),m.getCommunity().getName(),m.getStatus().name());
    }
    private Profile actor(String username) {
        return profiles.findUserByUsername(username)
            .orElseThrow(()->new AccessDeniedException("Utilizador desconhecido"));
    }
    private SportsTeam team(Long id) {
        return teams.findById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Equipa não encontrada"));
    }
    private Community community(Long id) {
        return communities.findById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Comunidade não encontrada"));
    }
    private boolean organizer(Long communityId,Long profileId) {
        return communityMembers.findByCommunityIdAndProfileId(communityId,profileId)
            .map(m->m.getRole()==CommunityRole.ADMIN||m.getRole()==CommunityRole.OWNER)
            .orElse(false);
    }
    @Transactional(readOnly=true)
    public List<MembershipView> communityTeams(Long communityId,String username) {
        var person=actor(username);
        var target=community(communityId);
        boolean canManage=organizer(communityId,person.getId());
        boolean isMember=communityMembers.existsByCommunityIdAndProfileId(communityId,person.getId());
        if(target.getVisibility()==CommunityVisibility.PRIVATE&&!isMember)
            throw new AccessDeniedException("Comunidade privada");
        return memberships.findByCommunityIdOrderByIdAsc(communityId).stream()
            .filter(m->canManage||m.getStatus()==SportsTeamCommunityMembership.Status.ACTIVE
                ||m.getTeam().getOwner().getId().equals(person.getId()))
            .map(this::view).toList();
    }
    @Transactional(readOnly=true)
    public List<MembershipView> teamCommunities(Long teamId,String username) {
        var person=actor(username);
        var selected=team(teamId);
        boolean isOwner=selected.getOwner().getId().equals(person.getId());
        return memberships.findByTeamIdOrderByIdDesc(teamId).stream()
            .filter(m->isOwner||m.getStatus()==SportsTeamCommunityMembership.Status.ACTIVE)
            .filter(m->isOwner || m.getCommunity().getVisibility()==CommunityVisibility.PUBLIC
                ||communityMembers.existsByCommunityIdAndProfileId(m.getCommunity().getId(),person.getId()))
            .map(this::view).toList();
    }
    @Transactional
    public MembershipView invite(Long communityId,Long teamId,String username) {
        var person=actor(username);
        if(!organizer(communityId,person.getId()))
            throw new AccessDeniedException("Só administradores da comunidade podem convidar equipas");
        return create(team(teamId),community(communityId),person,
            SportsTeamCommunityMembership.Status.PENDING_TEAM);
    }
    @Transactional
    public MembershipView request(Long teamId,Long communityId,String username) {
        var person=actor(username);
        var selected=team(teamId);
        if(!selected.getOwner().getId().equals(person.getId()))
            throw new AccessDeniedException("Só o responsável pode pedir adesão");
        var destination=community(communityId);
        if(destination.getVisibility()!=CommunityVisibility.PUBLIC)
            throw new AccessDeniedException("A comunidade é privada; aguarda convite do administrador");
        return create(selected,destination,person,
            SportsTeamCommunityMembership.Status.PENDING_COMMUNITY);
    }
    private MembershipView create(SportsTeam team,Community community,Profile actor,
                                   SportsTeamCommunityMembership.Status pending) {
        var existing=memberships.findByTeamIdAndCommunityId(team.getId(),community.getId());
        if(existing.isPresent() && existing.get().getStatus()!=SportsTeamCommunityMembership.Status.DECLINED)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"A relação ou pedido já existe");
        var m=existing.orElseGet(()->SportsTeamCommunityMembership.builder()
            .team(team).community(community).build());
        m.setRequestedBy(actor);
        m.setStatus(pending);
        m.setRequestedAt(Instant.now());
        m.setDecidedAt(null);
        return view(memberships.saveAndFlush(m));
    }
    @Transactional
    public MembershipView respond(Long id,boolean accept,String username) {
        var person=actor(username);
        var m=memberships.findById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido não encontrado"));
        if(m.getStatus()!=SportsTeamCommunityMembership.Status.PENDING_TEAM
           &&m.getStatus()!=SportsTeamCommunityMembership.Status.PENDING_COMMUNITY)
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Pedido já respondido");
        if(m.getStatus()==SportsTeamCommunityMembership.Status.PENDING_TEAM) {
            if(!m.getTeam().getOwner().getId().equals(person.getId()))
                throw new AccessDeniedException("Só o responsável pela equipa pode aceitar este convite");
        } else if(!organizer(m.getCommunity().getId(),person.getId())) {
            throw new AccessDeniedException("Só administradores da comunidade podem responder");
        }
        m.setStatus(accept?SportsTeamCommunityMembership.Status.ACTIVE:
            SportsTeamCommunityMembership.Status.DECLINED);
        m.setDecidedAt(Instant.now());
        // No CommunityMember records, competition registrations or chat memberships are created.
        return view(memberships.saveAndFlush(m));
    }
    @Transactional
    public void leave(Long id,String username) {
        var person=actor(username);
        var m=memberships.findById(id)
            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Relação não encontrada"));
        if(!m.getTeam().getOwner().getId().equals(person.getId())
            &&!organizer(m.getCommunity().getId(),person.getId()))
            throw new AccessDeniedException("Sem permissão para remover a equipa");
        memberships.delete(m);
        memberships.flush();
    }
}
