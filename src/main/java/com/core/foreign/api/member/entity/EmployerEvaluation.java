package com.core.foreign.api.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class EmployerEvaluation {

    @EmbeddedId
    private EmployerEvaluationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employerId")  // EmployerEvaluationId의 employeeId와 매핑
    @JoinColumn(name = "employer_id")
    private Employer employer;  // 고용인

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("evaluationId") // EmployerEvaluationId의 evaluationId와 매핑
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;  // 평가.

    private Integer evaluationCount;

    public EmployerEvaluation(Employer employer, Evaluation evaluation) {
        this.id = new EmployerEvaluationId(employer.getId(), evaluation.getId());
        this.employer = employer;
        this.evaluation = evaluation;
        this.evaluationCount = 0;
    }


}

