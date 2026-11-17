package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SportsTeamRepository extends JpaRepository<SportsTeam, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select team from SportsTeam team where team.id = :id")
    java.util.Optional<SportsTeam> findForDeletion(
            @org.springframework.data.repository.query.Param("id") Long id);
    boolean existsBySlug(String slug);
    // Team cards always render ownerProfileId. Fetch the lightweight owner in
    // the same query rather than lazily issuing one SELECT per returned team.
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "owner")
    List<SportsTeam> findByOwnerIdOrderByIdDesc(Long ownerId);
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "owner")
    List<SportsTeam> findByNameContainingIgnoreCaseOrderByIdDesc(String name, Pageable pageable);
}
