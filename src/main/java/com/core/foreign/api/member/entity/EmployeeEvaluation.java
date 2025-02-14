package com.core.foreign.api.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class EmployeeEvaluation {
    @EmbeddedId
    private EmployeeEvaluationId id;

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("employeeId")  // EmployeeEvaluationId의 employeeId와 매핑
    @JoinColumn(name = "employee_id")
    private Employee employee;  // 피고용인

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("evaluationId") // EmployeeEvaluationId의 evaluationId와 매핑
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;  // 평가.

    private Integer evaluationCount;

    public EmployeeEvaluation(Employee employee, Evaluation evaluation) {
        this.id = new EmployeeEvaluationId(employee.getId(), evaluation.getId());
        this.employee = employee;
        this.evaluation = evaluation;
        this.evaluationCount = 0;
    }
}

