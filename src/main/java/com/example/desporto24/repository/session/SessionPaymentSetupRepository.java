package com.example.desporto24.repository.session;

import com.example.desporto24.model.session.SessionPaymentSetup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SessionPaymentSetupRepository extends JpaRepository<SessionPaymentSetup, Long> {

    /** Paid MB WAY games that started between `after` and `before` and were not reminded after the game yet. */
    @Query("""
            select p.sessionId from SessionPaymentSetup p, Session s
            where s.id = p.sessionId
              and p.method = 'MBWAY'
              and p.postGameReminderAt is null
              and s.gameDate > :after and s.gameDate <= :before
              and s.priceAmount > 0
              and s.status <> com.example.desporto24.enumeration.SessionStatus.CANCELLED
            order by s.gameDate asc
            """)
    List<Long> findDueForPostGameReminder(@Param("after") Date after, @Param("before") Date before, Pageable pageable);
}
