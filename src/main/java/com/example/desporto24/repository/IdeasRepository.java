package com.example.desporto24.repository;
import com.example.desporto24.model.Ideas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
@Transactional(readOnly = true)
public interface IdeasRepository extends JpaRepository<Ideas, Long> {
    @jakarta.persistence.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select i from Ideas i where i.id = :id")
    java.util.Optional<Ideas> findForReply(@org.springframework.data.repository.query.Param("id") Long id);
}
