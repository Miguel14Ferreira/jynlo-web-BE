package com.example.desporto24.repository.community;

import com.example.desporto24.model.community.TournamentEntryPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentEntryPaymentRepository extends JpaRepository<TournamentEntryPayment, Long> {
    List<TournamentEntryPayment> findByTournamentId(Long tournamentId);
    Optional<TournamentEntryPayment> findByTournamentIdAndProfileId(Long tournamentId, Long profileId);
}
