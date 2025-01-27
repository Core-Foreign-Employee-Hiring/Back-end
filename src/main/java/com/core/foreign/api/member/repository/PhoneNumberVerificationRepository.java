package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.PhoneNumberVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneNumberVerificationRepository extends JpaRepository<PhoneNumberVerification, Long> {
    Optional<PhoneNumberVerification> findByPhoneNumber(String phoneNumber);

    Optional<PhoneNumberVerification> findByCode(String code);
}