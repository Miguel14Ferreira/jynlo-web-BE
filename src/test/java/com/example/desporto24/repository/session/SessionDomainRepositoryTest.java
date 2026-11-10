package com.example.desporto24.repository.session;

import com.example.desporto24.enumeration.AttendanceStatus;
import com.example.desporto24.enumeration.ParticipantState;
import com.example.desporto24.model.Profile;
import com.example.desporto24.model.Session;
import com.example.desporto24.model.community.Community;
import com.example.desporto24.model.community.CommunitySeason;
import com.example.desporto24.enumeration.CommunityVisibility;
import com.example.desporto24.repository.community.CommunityRepository;
import com.example.desporto24.repository.community.CommunitySeasonRepository;
import jakarta.persistence.EntityManager;
import com.example.desporto24.model.session.SessionParticipant;
import com.example.desporto24.model.session.SessionWaitlist;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:session-domain;MODE=LEGACY;NON_KEYWORDS=SESSION;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.hikari.schema=PUBLIC"
})
class SessionDomainRepositoryTest {

    @Autowired private ProfileRepository profileRepository;
    @Autowired private SessionRepository sessionRepository;
    @Autowired private CommunityRepository communityRepository;
    @Autowired private CommunitySeasonRepository seasonRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private SessionParticipantRepository participantRepository;
    @Autowired private SessionWaitlistRepository waitlistRepository;

    private Profile profile;
    private Session session;

    @BeforeEach
    void setUp() {
        profile = new Profile();
        profile.setUsername("miguel");
        profile.setUserId("1234567890");
        profile = profileRepository.saveAndFlush(profile);

        session = new Session();
        session.setSport("Futebol");
        session.setGameDate(new Date(System.currentTimeMillis() + 3_600_000));
        session.setCity("Cacem");
        session.setProfile(profile);
        session = sessionRepository.saveAndFlush(session);
    }

    @Test
    void participantMembershipIsUniquePerSessionAndProfile() {
        participantRepository.saveAndFlush(participant());
        assertThrows(DataIntegrityViolationException.class,
                () -> participantRepository.saveAndFlush(participant()));
    }

    @Test
    void waitlistMembershipIsUniquePerSessionAndProfile() {
        waitlistRepository.saveAndFlush(waitlist());
        assertThrows(DataIntegrityViolationException.class,
                () -> waitlistRepository.saveAndFlush(waitlist()));
    }

    @Test
    void deletingSeasonDetachesSessionsButKeepsTheirAttendanceAndOtherSeasons() {
        Community community = Community.builder()
                .name("Season FK regression")
                .visibility(CommunityVisibility.PRIVATE)
                .owner(profile)
                .createdAt(Instant.now())
                .build();
        community = communityRepository.saveAndFlush(community);

        CommunitySeason deleted = seasonRepository.saveAndFlush(CommunitySeason.builder()
                .community(community).name("Época anterior").active(false)
                .startsAt(Instant.now()).createdAt(Instant.now()).build());
        CommunitySeason retained = seasonRepository.saveAndFlush(CommunitySeason.builder()
                .community(community).name("Época atual").active(true)
                .startsAt(Instant.now()).createdAt(Instant.now()).build());

        session.setSeasonCompetition(deleted);
        sessionRepository.saveAndFlush(session);
        Session other = new Session();
        other.setSport("Futebol");
        other.setGameDate(new Date(System.currentTimeMillis() + 7_200_000));
        other.setCity("Cacem");
        other.setProfile(profile);
        other.setSeasonCompetition(retained);
        other = sessionRepository.saveAndFlush(other);
        participantRepository.saveAndFlush(participant());

        Long oldGameId = session.getId();
        Long otherGameId = other.getId();
        Long retainedSeasonId = retained.getId();
        Long deletedSeasonId = deleted.getId();
        entityManager.flush();
        entityManager.clear();

        assertEquals(1, sessionRepository.detachDeletedSeason(deletedSeasonId));
        entityManager.flush();
        entityManager.clear();
        seasonRepository.deleteById(deletedSeasonId);
        seasonRepository.flush();
        entityManager.clear();

        var oldGame = sessionRepository.findById(oldGameId).orElseThrow();
        assertNull(oldGame.getSeasonCompetition());
        assertTrue(participantRepository.findAll().stream()
                .anyMatch(attendee -> attendee.getSession().getId().equals(oldGameId)));
        var currentGame = sessionRepository.findById(otherGameId).orElseThrow();
        assertEquals(retainedSeasonId, currentGame.getSeasonCompetition().getId());
        assertTrue(seasonRepository.findById(retainedSeasonId).isPresent());
        assertTrue(seasonRepository.findById(deletedSeasonId).isEmpty());
    }

    private SessionParticipant participant() {
        SessionParticipant participant = new SessionParticipant();
        participant.setSession(session);
        participant.setProfile(profile);
        participant.setState(ParticipantState.ACTIVE);
        participant.setAttendanceStatus(AttendanceStatus.PENDING);
        participant.setJoinedAt(Instant.now());
        return participant;
    }

    private SessionWaitlist waitlist() {
        SessionWaitlist waitlist = new SessionWaitlist();
        waitlist.setSession(session);
        waitlist.setProfile(profile);
        waitlist.setPriorityTier(0);
        waitlist.setCreatedAt(Instant.now());
        return waitlist;
    }
}
