package com.example.desporto24.service;

import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.community.CommunityMemberRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.service.interfaces.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/**
 * Public-only association projection. Never return member rosters, invitations,
 * private community names, account-only lists or unapproved memberships.
 */
@Service
@RequiredArgsConstructor
public class PublicProfileAffiliationsService {
    private final ProfileRepository profiles;
    private final ProfileService profileService;
    private final SportsTeamMemberRepository teamMembers;
    private final CommunityMemberRepository communityMembers;

    public record TeamLink(Long id, String name, String sport) {}
    public record CommunityLink(Long id, String name, String sport) {}
    public record Affiliations(List<TeamLink> teams, List<CommunityLink> communities) {}

    @Transactional(readOnly = true)
    public Affiliations visible(String actorUsername, String targetUsername) {
        // Keep the exact block policy used by the existing authenticated public
        // profile endpoint, including either direction of a user block.
        profileService.findPublicProfile(actorUsername, targetUsername);
        var target = profiles.findUserByUsernameIgnoreCase(targetUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // A private profile never reveals its affiliations to third parties.
        // The owner's personal /joined and /mine APIs serve that use case.
        if (target.isPrivateProfile())
            return new Affiliations(List.of(), List.of());

        var teams = teamMembers.findByProfileIdAndStatusOrderByIdDesc(
                target.getId(), SportsTeamMember.Status.ACTIVE).stream()
                .map(member -> member.getTeam())
                .filter(Objects::nonNull)
                .distinct()
                .limit(20)
                .map(team -> new TeamLink(team.getId(), team.getName(), team.getSport()))
                .toList();

        var communities = communityMembers.findByProfileIdOrderByJoinedAtDesc(
                target.getId()).stream()
                .map(member -> member.getCommunity())
                .filter(community -> community != null
                        && community.getVisibility() == CommunityVisibility.PUBLIC)
                .distinct()
                .limit(20)
                .map(community -> new CommunityLink(community.getId(),
                        community.getName(), community.getSport()))
                .toList();

        return new Affiliations(teams, communities);
    }
}
