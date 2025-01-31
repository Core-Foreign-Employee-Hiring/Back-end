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
    Optional<Member> findByUserIdAndEmailAndName(String userId, String email, String name);
    Optional<Member> findByNameAndPhoneNumber(String name, String phoneNumber);


    @Transactional
    @Modifying
    @Query("update Employer e set e.companyImageUrl = :companyImageUrl where e.id = :employerId")
    void updateCompanyImage(@Param("employerId") Long employerId, @Param("companyImageUrl") String companyImageUrl);

    @Modifying
    @Query("update Member m set m.userId=:userId where m.id=:memberId")
    void updateUserId(@Param("memberId") Long memberId, @Param("userId") String userId);

}
