package com.example.desporto24.repository.venue;

import com.example.desporto24.model.venue.VenueOpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueOpeningHoursRepository extends JpaRepository<VenueOpeningHours, Long> {
    List<VenueOpeningHours> findByVenueIdOrderByDayOfWeekAscOpenTimeAsc(Long venueId);
    void deleteByVenueId(Long venueId);
}
