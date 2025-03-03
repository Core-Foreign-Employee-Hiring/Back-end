package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmployerResume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployerResumeRepository extends JpaRepository<EmployerResume, Long> {

    @Query("select er from EmployerResume er" +
            " where er.employer.id= :employerId and er.resume.id= :resumeId")
    Optional<EmployerResume> findByEmployerIdAndResumeId(@Param("employerId")Long employerId, @Param("resumeId")Long resumeId);


    @Query("select er from EmployerResume er" +
            " join fetch er.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit" +
            " where er.employer.id= :employerId")
    Page<EmployerResume> findByEmployerId(@Param("employerId")Long employerId, Pageable pageable);


    boolean existsByEmployerIdAndResumeId(@Param("employerId")Long employerId, @Param("resumeId")Long resumeId);
}
