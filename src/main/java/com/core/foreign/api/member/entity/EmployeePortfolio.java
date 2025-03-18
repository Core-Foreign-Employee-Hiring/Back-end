package com.core.foreign.api.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class EmployeePortfolio {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String introduction; // 자기소개

    private String enrollmentCertificateUrl; // 재학증명서
    private String transcriptUrl; // 성적증명서
    private String partTimeWorkPermitUrl; // 시간제근로허가서

    @Enumerated(STRING)
    private Topic topic;


    @Enumerated(STRING)
    private EmployeePortfolioStatus employeePortfolioStatus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;


    @Builder.Default
    @OneToMany(fetch=LAZY, mappedBy = "employeePortfolio", cascade = REMOVE)
    private List<EmployeePortfolioBusinessFieldInfo> employeePortfolioBusinessFieldInfos = new ArrayList<>();


    public void updateExceptBusinessFieldInfo(String introduction,
                                              String enrollmentCertificateUrl, String transcriptUrl, String partTimeWorkPermitUrl,
                                              Topic topic) {
        this.introduction = introduction;
        this.enrollmentCertificateUrl = enrollmentCertificateUrl;
        this.transcriptUrl = transcriptUrl;
        this.partTimeWorkPermitUrl = partTimeWorkPermitUrl;
        this.topic = topic;

    }
}
