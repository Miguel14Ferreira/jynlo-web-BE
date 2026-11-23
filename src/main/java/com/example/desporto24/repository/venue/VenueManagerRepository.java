package com.example.desporto24.repository.venue;

import com.example.desporto24.model.venue.VenueManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueManagerRepository extends JpaRepository<VenueManager, Long> {
    boolean existsByVenueIdAndUsernameIgnoreCase(Long venueId, String username);
    List<VenueManager> findByUsernameIgnoreCase(String username);
    List<VenueManager> findByVenueId(Long venueId);
}
