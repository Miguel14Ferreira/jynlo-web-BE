package com.example.desporto24.repository.team;

import com.example.desporto24.model.team.SportsTeamContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SportsTeamContentRepository extends JpaRepository<SportsTeamContent, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "author")
    List<SportsTeamContent> findByTeamIdAndKindOrderByIdDesc(
            Long teamId, SportsTeamContent.Kind kind, Pageable pageable);
    /** Cursor pagination keeps historical team content scoped to one team and kind. */
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "author")
    List<SportsTeamContent> findByTeamIdAndKindAndIdLessThanOrderByIdDesc(
            Long teamId, SportsTeamContent.Kind kind, Long beforeId, Pageable pageable);
    /** Server-side search is scoped to the requested team and content type. */
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "author")
    @org.springframework.data.jpa.repository.Query("""
        select content from SportsTeamContent content
        where content.team.id = :teamId and content.kind = :kind
          and content.deletedAt is null
          and locate(lower(:term), lower(content.body)) > 0
        order by content.id desc
        """)
    List<SportsTeamContent> searchVisible(
            @org.springframework.data.repository.query.Param("teamId") Long teamId,
            @org.springframework.data.repository.query.Param("kind") SportsTeamContent.Kind kind,
            @org.springframework.data.repository.query.Param("term") String term, Pageable pageable);

    boolean existsByTeamId(Long teamId);

    /** Latest visible entry, for the inbox preview of a team chat. */
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "author")
    List<SportsTeamContent> findByTeamIdAndKindAndDeletedAtIsNullOrderByIdDesc(
            Long teamId, SportsTeamContent.Kind kind, Pageable pageable);

    /** Unread count for one member: visible entries after the read cursor, not written by them. */
    @org.springframework.data.jpa.repository.Query("""
        select count(content) from SportsTeamContent content
        where content.team.id = :teamId and content.kind = :kind
          and content.deletedAt is null and content.id > :afterId
          and content.author.id <> :viewerId
        """)
    long countUnread(
            @org.springframework.data.repository.query.Param("teamId") Long teamId,
            @org.springframework.data.repository.query.Param("kind") SportsTeamContent.Kind kind,
            @org.springframework.data.repository.query.Param("afterId") long afterId,
            @org.springframework.data.repository.query.Param("viewerId") Long viewerId);
}