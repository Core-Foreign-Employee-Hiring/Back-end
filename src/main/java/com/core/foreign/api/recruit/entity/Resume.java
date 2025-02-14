package com.core.foreign.api.recruit.entity;


import com.core.foreign.api.member.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String messageToEmployer;

    @ManyToOne(fetch = LAZY)
    private Recruit recruit;

    @ManyToOne(fetch = LAZY)
    private Employee employee;

    @Enumerated(STRING)
    private ApplyMethod applyMethod;

    @Enumerated(STRING)
    private RecruitmentStatus recruitmentStatus;

    @Enumerated(STRING)
    private EvaluationStatus evaluationStatus;
    private LocalDate approvedAt;

    private boolean isEmployeeEvaluatedByEmployer; // 고용인이 피고용인을 평가했는지 여부
    private boolean isEmployerEvaluatedByEmployee; // 피고용인이 고용인을 평가했는지 여부


    @Enumerated(STRING)
    private ContractStatus contractStatus;

    private boolean isDeleted;

    public void delete(){
        this.isDeleted = true;
    }


    public void approve() {
        this.recruitmentStatus = RecruitmentStatus.APPROVED;
        this.approvedAt = LocalDate.now();
    }

    public void reject(){
        this.recruitmentStatus=RecruitmentStatus.REJECTED;
    }


    public void evaluateEmployer(){
        this.isEmployerEvaluatedByEmployee=true;
    }

    public void evaluateEmployee(){
        this.isEmployeeEvaluatedByEmployer=true;
    }
}
