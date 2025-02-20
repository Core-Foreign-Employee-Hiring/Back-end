package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.PremiumManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PremiumManageRepository extends JpaRepository<PremiumManage, Long> {
    Optional<PremiumManage> findByEmployerId(Long employerId);

    // 프리미엄 공고 등록 횟수 증가
    @Transactional
    @Modifying
    @Query("UPDATE PremiumManage pm SET pm.premiumCount = pm.premiumCount + 1 WHERE pm.employer.id = :employerId")
    int increasePremiumCount(@Param("employerId") Long employerId);

    // 프리미엄 공고 등록 횟수 감소
    @Transactional
    @Modifying
    @Query("UPDATE PremiumManage pm SET pm.premiumCount = pm.premiumCount - 1 WHERE pm.employer.id = :employerId AND pm.premiumCount > 0")
    int decreasePremiumCount(@Param("employerId") Long employerId);

    // 프리미엄 상단점프 카운트 증가
    @Transactional
    @Modifying
    @Query("UPDATE PremiumManage pm SET pm.premiumJumpCount = pm.premiumJumpCount + 3 WHERE pm.employer.id = :employerId")
    int increasePremiumJumpCount(@Param("employerId") Long employerId);

    // 일반 상단점프 카운트 증가
    @Transactional
    @Modifying
    @Query("UPDATE PremiumManage pm SET pm.normalJumpCount = pm.normalJumpCount + 3 WHERE pm.employer.id = :employerId")
    int increaseNormalJumpCount(@Param("employerId") Long employerId);

    // 프리미엄 상단점프 카운트 감소
    @Transactional
    @Modifying
    @Query("UPDATE PremiumManage pm SET pm.premiumJumpCount = pm.premiumJumpCount - 1 WHERE pm.employer.id = :employerId AND pm.premiumJumpCount > 0")
    int decreasePremiumJumpCount(@Param("employerId") Long employerId);

    // 일반 상단점프 카운트 감소
    @Transactional
    @Modifying
    @Query("UPDATE PremiumManage pm SET pm.normalJumpCount = pm.normalJumpCount - 1 WHERE pm.employer.id = :employerId AND pm.normalJumpCount > 0")
    int decreaseNormalJumpCount(@Param("employerId") Long employerId);
}
