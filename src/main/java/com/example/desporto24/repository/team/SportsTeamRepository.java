package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SportsTeamRepository extends JpaRepository<SportsTeam, Long> {
    boolean existsBySlug(String slug);
    List<SportsTeam> findByOwnerIdOrderByIdDesc(Long ownerId);
    List<SportsTeam> findByNameContainingIgnoreCaseOrderByIdDesc(String name, Pageable pageable);
}
