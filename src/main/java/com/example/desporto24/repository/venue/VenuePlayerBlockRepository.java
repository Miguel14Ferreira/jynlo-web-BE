package com.example.desporto24.repository.venue;

import com.example.desporto24.model.venue.VenuePlayerBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenuePlayerBlockRepository extends JpaRepository<VenuePlayerBlock, Long> {
    boolean existsByVenueIdAndUsernameIgnoreCase(Long venueId, String username);
    Optional<VenuePlayerBlock> findFirstByVenueIdAndUsernameIgnoreCase(Long venueId, String username);
    List<VenuePlayerBlock> findTop200ByVenueIdOrderByCreatedAtDesc(Long venueId);
}
