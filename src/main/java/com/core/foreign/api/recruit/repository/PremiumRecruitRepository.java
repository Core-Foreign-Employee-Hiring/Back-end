package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.PremiumRecruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PremiumRecruitRepository extends JpaRepository<PremiumRecruit, Long> {

    @Query("select p from PremiumRecruit p" +
            " left join p.portfolios" +
            " where p.id=:premiumRecruitId")
    Optional<PremiumRecruit> findPremiumRecruitWithPortfolioById(@Param("premiumRecruitId")Long premiumRecruitId);
}
