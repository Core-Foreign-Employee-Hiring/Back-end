package com.core.foreign.api.recruit.entity;


import com.core.foreign.api.member.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(STRING)
    private ContractStatus contractStatus;

    public void approve() {
        this.recruitmentStatus = RecruitmentStatus.APPROVED;
    }

    public void reject(){
        this.recruitmentStatus=RecruitmentStatus.REJECTED;
    }
}
