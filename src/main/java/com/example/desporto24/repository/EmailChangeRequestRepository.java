package com.example.desporto24.repository;

import com.example.desporto24.model.EmailChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, String> {
    @Modifying
    @Query("delete from EmailChangeRequest r where lower(r.username) = lower(:username) and r.usedAt is null")
    int deletePendingFor(@Param("username") String username);
}
