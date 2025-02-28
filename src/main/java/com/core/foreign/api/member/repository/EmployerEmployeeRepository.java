package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.EmployerEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployerEmployeeRepository extends JpaRepository<EmployerEmployee, Long> {

    @Query("select ee from EmployerEmployee ee" +
            " where ee.employer.id= :employerId and ee.employee.id= :employeeId")
    Optional<EmployerEmployee> findByEmployerIdAndEmployeeId(@Param("employerId")Long employerId, @Param("employeeId")Long employeeId);

    @Query("select ee from EmployerEmployee ee" +
            " join fetch ee.employee" +
            " where ee.employer.id= :employerId")
    Page<EmployerEmployee> findByEmployerId(@Param("employerId")Long employerId, Pageable pageable);
}
