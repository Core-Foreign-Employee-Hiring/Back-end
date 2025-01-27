package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmailDuplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailDuplicationRepository extends JpaRepository<EmailDuplication, Long> {

    Optional<EmailDuplication> findByEmail(String email);


    @Modifying
    @Query("delete from EmailDuplication e where e.email=:email")
    void deleteByEmail(@Param("email")String email);
}
