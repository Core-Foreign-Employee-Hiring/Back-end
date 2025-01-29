package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeePortfolioBusinessFieldInfoRepository extends JpaRepository<EmployeePortfolioBusinessFieldInfo, Long> {

    @Modifying
    @Transactional
    @Query("delete from EmployeePortfolioBusinessFieldInfo i where i.employeePortfolio.id=:employeePortfolioId")
    void deleteByEmployeePortfolioId(@Param("employeePortfolioId")Long employeePortfolioId);
}
