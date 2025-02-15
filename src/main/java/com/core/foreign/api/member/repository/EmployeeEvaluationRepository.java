package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EmployeeEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeEvaluationRepository extends JpaRepository<EmployeeEvaluation, Long> {

    @Modifying
    @Query("update EmployeeEvaluation e set e.evaluationCount=e.evaluationCount+1 where e.id.employeeId=:employeeId and e.id.evaluationId=:evaluationId")
    void evaluateEmployee(@Param("employeeId")Long employeeId, @Param("evaluationId")Long evaluationId);

    @Query("select ee from EmployeeEvaluation ee" +
            " join fetch ee.evaluation" +
            " where ee.employee.id=:employeeId")
    List<EmployeeEvaluation> findByEmployeeId(@Param("employeeId") Long employeeId);


    @Query("select ee from EmployeeEvaluation ee" +
            " join fetch ee.evaluation" +
            " join fetch ee.employee" +
            " where ee.employee.id in :ids")
    List<EmployeeEvaluation> findByEmployeeIds(@Param("ids") List<Long> ids);

}
