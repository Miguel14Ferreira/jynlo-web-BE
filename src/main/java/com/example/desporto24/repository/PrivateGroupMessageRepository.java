package com.example.desporto24.repository;
import com.example.desporto24.model.PrivateGroupMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.*;
public interface PrivateGroupMessageRepository extends JpaRepository<PrivateGroupMessage,Long> {
    List<PrivateGroupMessage> findByGroupIdAndCreatedAtGreaterThanEqualOrderByIdDesc(
        Long groupId,Instant since,Pageable pageable);
    List<PrivateGroupMessage> findByGroupIdAndCreatedAtGreaterThanEqualAndIdLessThanOrderByIdDesc(
        Long groupId,Instant since,Long beforeId,Pageable pageable);
    /** Unread for a member: messages since they joined, after their read cursor, written by others. */
    @org.springframework.data.jpa.repository.Query("select count(m) from PrivateGroupMessage m where m.groupId = :groupId and m.createdAt >= :since and m.id > :afterId and m.deletedAt is null and lower(m.senderUsername) <> lower(:reader)")
    long countUnread(@org.springframework.data.repository.query.Param("groupId") Long groupId,
                     @org.springframework.data.repository.query.Param("since") Instant since,
                     @org.springframework.data.repository.query.Param("afterId") Long afterId,
                     @org.springframework.data.repository.query.Param("reader") String reader);
    List<PrivateGroupMessage> findBySenderUsernameIgnoreCase(String senderUsername);
    List<PrivateGroupMessage> findByGroupId(Long groupId);
    /** Escaped LIKE search (escape '!'), for the shared inbox search. */
    @org.springframework.data.jpa.repository.Query(value="""
        select m from PrivateGroupMessage m
        where m.groupId = :groupId and m.createdAt >= :since and m.deletedAt is null
          and lower(m.body) like concat('%', :term, '%') escape '!'
        order by m.id desc""")
    org.springframework.data.domain.Page<PrivateGroupMessage> searchConversation(
        @org.springframework.data.repository.query.Param("groupId") Long groupId,
        @org.springframework.data.repository.query.Param("since") Instant since,
        @org.springframework.data.repository.query.Param("term") String term, Pageable pageable);
    List<PrivateGroupMessage> findByGroupIdAndCreatedAtGreaterThanEqualAndIdLessThanEqualOrderByIdDesc(
        Long groupId,Instant since,Long id,Pageable pageable);
    List<PrivateGroupMessage> findByGroupIdAndCreatedAtGreaterThanEqualAndIdGreaterThanOrderByIdAsc(
        Long groupId,Instant since,Long id,Pageable pageable);
    /** Visible messages a member can search: since they joined, not deleted, text contains the term. */
    @org.springframework.data.jpa.repository.Query("""
        select m from PrivateGroupMessage m
        where m.groupId = :groupId and m.createdAt >= :since and m.deletedAt is null
          and locate(lower(:term), lower(m.body)) > 0
        order by m.id desc""")
    List<PrivateGroupMessage> searchVisible(@org.springframework.data.repository.query.Param("groupId") Long groupId,
        @org.springframework.data.repository.query.Param("since") Instant since,
        @org.springframework.data.repository.query.Param("term") String term, Pageable pageable);
}
