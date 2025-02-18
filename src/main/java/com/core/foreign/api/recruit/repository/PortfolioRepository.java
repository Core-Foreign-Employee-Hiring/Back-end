package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.sound.sampled.Port;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    @Query("select p from Portfolio p" +
            " where p.premiumRecruit.id=:recruitId")
    List<Portfolio> findByRecruitId(@Param("recruitId") Long recruitId);

}
