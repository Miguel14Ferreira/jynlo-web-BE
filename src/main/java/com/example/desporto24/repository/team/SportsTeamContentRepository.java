package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SportsTeamContentRepository extends JpaRepository<SportsTeamContent, Long> {
    List<SportsTeamContent> findByTeamIdAndKindOrderByIdDesc(
            Long teamId, SportsTeamContent.Kind kind, Pageable pageable);
}