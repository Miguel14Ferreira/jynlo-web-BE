package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.CommunityRoundEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRoundEntryRepository extends JpaRepository<CommunityRoundEntry, Long> {
    List<CommunityRoundEntry> findByRoundIdOrderByIdAsc(Long roundId);
    List<CommunityRoundEntry> findByRoundCommunityIdOrderByRoundPlayedAtAscRoundIdAscIdAsc(Long communityId);
    List<CommunityRoundEntry> findByRoundSeasonIdOrderByRoundPlayedAtAscRoundIdAscIdAsc(Long seasonId);
    // Only rounds attributed to a linked registered profile; historical records without
    // an account stay anonymous and cannot be attributed to an arbitrary player.
    @Query("""
        select entry from CommunityRoundEntry entry
        join fetch entry.communityPlayer player
        where entry.profile.id = :profileId
           or (entry.profile is null and player.profile.id = :profileId)
        """)
    List<CommunityRoundEntry> findEntriesForProfile(@Param("profileId") Long profileId);

    /** Entries of rounds played in a community between two instants (e.g. one month). */
    @Query("select entry from CommunityRoundEntry entry join fetch entry.communityPlayer player " +
           "where entry.round.community.id = :communityId and entry.round.playedAt >= :from and entry.round.playedAt < :to")
    List<CommunityRoundEntry> findCommunityEntriesBetween(@Param("communityId") Long communityId,
            @Param("from") java.time.Instant from, @Param("to") java.time.Instant to);
    void deleteByRoundId(Long roundId);

    /** Player IDs linked to this exact season before its entries are deleted. */
    @Query("select distinct entry.communityPlayer.id from CommunityRoundEntry entry " +
           "where entry.round.community.id = :communityId and entry.round.season.id = :seasonId")
    List<Long> findHistoricalPlayerIdsBySeason(
            @Param("communityId") Long communityId, @Param("seasonId") Long seasonId);

    boolean existsByRoundCommunityId(Long communityId);
    boolean existsByCommunityPlayerId(Long playerId);
}
