package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.ResumePortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResumePortfolioRepository extends JpaRepository<ResumePortfolio, Integer> {

    @Query("select rp from ResumePortfolio rp" +
            " where rp.resume.id=:resumeId")
    List<ResumePortfolio> findByResumeId(@Param("resumeId")Long resumeId);
}
