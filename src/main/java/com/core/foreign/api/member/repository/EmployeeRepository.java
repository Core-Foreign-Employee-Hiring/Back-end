package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findAllBy(Pageable pageable);
}
