package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("select e from Employee e" +
            " where e.isPortfolioPublic=true and e.isWithdrawn=false")
    Page<Employee> findAllBy(Pageable pageable);

    @Query("select e from Employee e" +
            " where e.id=:employeeId and  e.isPortfolioPublic =true and e.isWithdrawn=false")
    Optional<Employee> findPublicEmployeeById(@Param("employeeId")Long employeeId);

    @Modifying
    @Query("update Employee employee set employee.viewCount=employee.viewCount+1 where employee.id=:employeeId")
    void increaseViewCount(@Param("employeeId")Long employeeId);
}
