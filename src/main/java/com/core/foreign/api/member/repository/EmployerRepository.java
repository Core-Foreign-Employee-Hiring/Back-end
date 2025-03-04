package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.dto.EmployerReliabilityDTO;
import com.core.foreign.api.member.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    @Query(value = "SELECT " +
            "sum(CASE WHEN r2.RECRUITMENT_STATUS = 'approved' THEN 1 ELSE 0 END), " +
            "sum(CASE WHEN r2.CONTRACT_STATUS = 'COMPLETED' THEN 1 ELSE 0 END) " +
            "FROM employer e " +
            "JOIN recruit r1 ON r1.employer_id = e.id " +
            "JOIN resume r2 ON r2.recruit_id = r1.id " +
            "WHERE e.id = :employerId "
            , nativeQuery = true)
    EmployerReliabilityDTO getEmployerReliability(@Param("employerId") Long employerId);
}
