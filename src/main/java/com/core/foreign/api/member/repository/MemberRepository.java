package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);


    @Transactional
    @Modifying
    @Query("update Employer e set e.companyImageUrl = :companyImageUrl where e.id = :employerId")
    void updateCompanyImage(@Param("employerId") Long employerId, @Param("companyImageUrl") String companyImageUrl);

}
