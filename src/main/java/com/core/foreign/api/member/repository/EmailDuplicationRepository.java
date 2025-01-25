package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmailDuplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailDuplicationRepository extends JpaRepository<EmailDuplication, Long> {

    @Modifying
    @Query("delete from EmailDuplication e where e.email=:email")
    void deleteByEmail(@Param("email")String email);
}
