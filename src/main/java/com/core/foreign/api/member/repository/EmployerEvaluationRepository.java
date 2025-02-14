package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmployerEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployerEvaluationRepository extends JpaRepository<EmployerEvaluation, Long> {
    @Modifying
    @Query("update EmployerEvaluation e set e.evaluationCount=e.evaluationCount+1 where e.id.employerId=:employerId and e.id.evaluationId=:evaluationId")
    void evaluateEmployer(@Param("employerId")Long employerId, @Param("evaluationId")Long evaluationId);

    @Query("select ee from EmployerEvaluation ee" +
            " join fetch ee.evaluation" +
            " where ee.employer.id=:employerId")
    List<EmployerEvaluation> findByEmployerId(@Param("employerId") Long employerId);
}
