package com.example.desporto24.model.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Opening hours used to offer booking slots; day 1 = Monday … 7 = Sunday, times "HH:mm" in Lisbon time. */
@Entity
@Table(name = "venue_opening_hours")
@Getter @Setter
public class VenueOpeningHours {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "venue_id", nullable = false)
    private Long venueId;

    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "open_time", nullable = false, length = 5)
    private String openTime;

    @Column(name = "close_time", nullable = false, length = 5)
    private String closeTime;
}
