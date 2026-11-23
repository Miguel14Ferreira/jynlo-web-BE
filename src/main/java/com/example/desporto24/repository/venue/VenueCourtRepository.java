package com.example.desporto24.repository.venue;

import com.example.desporto24.model.venue.VenueCourt;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VenueCourtRepository extends JpaRepository<VenueCourt, Long> {
    List<VenueCourt> findByVenueIdOrderByIdAsc(Long venueId);
    List<VenueCourt> findByVenueIdAndActiveTrueOrderByIdAsc(Long venueId);
    long countByVenueId(Long venueId);

    /** Serialises booking requests for one court so two people cannot take the same slot. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from VenueCourt c where c.id = :id")
    Optional<VenueCourt> lockById(@Param("id") Long id);

    /** Venues that have at least one active court, opening hours and a confirmed manager. */
    @Query("select distinct c.venueId from VenueCourt c where c.active = true"
            + " and exists (select 1 from VenueOpeningHours h where h.venueId = c.venueId)"
            + " and exists (select 1 from VenueManager m where m.venueId = c.venueId)")
    List<Long> venuesAcceptingBookings();
}
