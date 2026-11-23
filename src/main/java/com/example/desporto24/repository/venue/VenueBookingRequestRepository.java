package com.example.desporto24.repository.venue;

import com.example.desporto24.model.venue.VenueBookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface VenueBookingRequestRepository extends JpaRepository<VenueBookingRequest, Long> {
    List<VenueBookingRequest> findTop100ByRequesterUsernameIgnoreCaseOrderByStartAtDesc(String username);
    List<VenueBookingRequest> findTop200ByVenueIdAndStartAtAfterOrderByStartAtAsc(Long venueId, Instant after);

    /** Requests that still hold a slot (pending or confirmed) and overlap [from, to). */
    @Query("select b from VenueBookingRequest b where b.courtId = :courtId and b.status in :statuses"
            + " and b.startAt < :to and b.endAt > :from")
    List<VenueBookingRequest> overlapping(@Param("courtId") Long courtId, @Param("from") Instant from,
                                          @Param("to") Instant to,
                                          @Param("statuses") Collection<VenueBookingRequest.Status> statuses);

    @Query("select b from VenueBookingRequest b where b.venueId = :venueId and b.status in :statuses"
            + " and b.startAt < :to and b.endAt > :from")
    List<VenueBookingRequest> holdingInVenue(@Param("venueId") Long venueId, @Param("from") Instant from,
                                             @Param("to") Instant to,
                                             @Param("statuses") Collection<VenueBookingRequest.Status> statuses);

    long countByRequesterUsernameIgnoreCaseAndStatusAndStartAtAfter(
            String username, VenueBookingRequest.Status status, Instant after);
}
