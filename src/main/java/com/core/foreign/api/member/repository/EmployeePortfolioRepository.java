package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmployeePortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeePortfolioRepository extends JpaRepository<EmployeePortfolio, Long> {

    @Query("select p from EmployeePortfolio p" +
            " left join fetch p.employeePortfolioBusinessFieldInfos" +
            " join fetch p.employee" +
            " where p.employee.id=:employeeId")
    Optional<EmployeePortfolio> findByEmployeeId(@Param("employeeId")Long employeeId);

    @Query("select p from EmployeePortfolio p" +
            " left join fetch p.employeePortfolioBusinessFieldInfos" +
            " where p.employee.id=:employeeId")
    Optional<EmployeePortfolio> findEmployeePortfolioByEmployeeId(@Param("employeeId")Long employeeId);

}
