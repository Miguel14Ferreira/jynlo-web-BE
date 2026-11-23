package com.example.desporto24.repository.venue;

import com.example.desporto24.model.venue.VenueOwnershipClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenueOwnershipClaimRepository extends JpaRepository<VenueOwnershipClaim, Long> {
    List<VenueOwnershipClaim> findByStatusOrderByCreatedAtAsc(VenueOwnershipClaim.Status status);
    List<VenueOwnershipClaim> findTop50ByStatusNotOrderByDecidedAtDesc(VenueOwnershipClaim.Status status);
    Optional<VenueOwnershipClaim> findFirstByVenueIdAndUsernameIgnoreCaseAndStatus(
            Long venueId, String username, VenueOwnershipClaim.Status status);
    Optional<VenueOwnershipClaim> findFirstByVenueIdAndUsernameIgnoreCaseOrderByCreatedAtDesc(Long venueId, String username);
    long countByUsernameIgnoreCaseAndStatus(String username, VenueOwnershipClaim.Status status);
}
