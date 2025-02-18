package com.core.foreign.api.recruit.entity;


import com.core.foreign.api.contract.entity.ContractStatus;
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
    private LocalDate approvedAt;

    @Enumerated(STRING)
    private EvaluationStatus isEmployeeEvaluatedByEmployer; // 고용인이 피고용인을 평가했는지 여부
    private LocalDate employeeEvaluationDate; // 고용인이 피고용인을 평가한 날짜

    @Enumerated(STRING)
    private EvaluationStatus isEmployerEvaluatedByEmployee; // 피고용인이 고용인을 평가했는지 여부
    private LocalDate employerEvaluationDate; // 피고용인이 고용인을 평가한 날짜


    private boolean isPublic;  // 프리미엄 공고일 때만 유효함.


    @Enumerated(STRING)
    private ContractStatus contractStatus;

    private boolean isDeleted;

    public void delete(){
        this.isDeleted = true;
    }


    public void approve() {
        this.recruitmentStatus = RecruitmentStatus.APPROVED;
        this.approvedAt = LocalDate.now();
        this.isEmployeeEvaluatedByEmployer=EvaluationStatus.NOT_EVALUATED;
    }

    public void reject(){
        this.recruitmentStatus=RecruitmentStatus.REJECTED;
    }


    public void evaluateEmployer(){
        this.isEmployerEvaluatedByEmployee=EvaluationStatus.COMPLETED;
        this.employerEvaluationDate = LocalDate.now();
    }

    public void evaluateEmployee(){
        this.isEmployeeEvaluatedByEmployer=EvaluationStatus.COMPLETED;
        this.employeeEvaluationDate=LocalDate.now();
    }

    public void makePublic() {
        this.isPublic = true;
    }

    public void makePrivate() {
        this.isPublic = false;
    }

}
