package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.PhoneNumberDuplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhoneNumberDuplicationRepository extends JpaRepository<PhoneNumberDuplication, Long> {
    @Modifying
    @Query("delete from PhoneNumberDuplication p where p.phoneNumber=:phoneNumber")
    void deleteByPhoneNumber(@Param("phoneNumber")String phoneNumber);
}
