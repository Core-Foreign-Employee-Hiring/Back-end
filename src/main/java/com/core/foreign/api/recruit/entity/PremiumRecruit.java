package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.recruit.dto.RecruitRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "premium_recruit")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PremiumRecruit extends Recruit {

    @OneToMany(mappedBy = "premiumRecruit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Portfolio> portfolios = new ArrayList<>();

    @Builder(toBuilder = true)
    public PremiumRecruit(
            String title,
            Member employer,
            Address address,
            Set<BusinessField> businessFields,
            Double latitude,
            Double longitude,
            LocalDate recruitStartDate,
            LocalDate recruitEndDate,
            Integer recruitCount,
            String gender,
            String education,
            String otherConditions,
            List<String> preferredConditions,
            List<String> workDuration,
            String workDurationOther,
            List<String> workTime,
            String workTimeOther,
            List<String> workDays,
            String workDaysOther,
            String salary,
            String salaryType,
            String salaryOther,
            Set<ApplyMethod> applicationMethods,
            String posterImageUrl,
            LocalDateTime jumpDate,
            RecruitPublishStatus recruitPublishStatus) {
        super(
                title,
                employer,
                address,
                businessFields,
                latitude,
                longitude,
                recruitStartDate,
                recruitEndDate,
                recruitCount,
                gender,
                education,
                otherConditions,
                preferredConditions,
                workDuration,
                workDurationOther,
                workTime,
                workTimeOther,
                workDays,
                workDaysOther,
                salary,
                salaryType,
                salaryOther,
                applicationMethods,
                RecruitType.PREMIUM,
                posterImageUrl,
                jumpDate,
                recruitPublishStatus
        );
    }

    public void addPortfolio(Portfolio portfolio) {
        portfolios.add(portfolio);
        portfolio.assignPremiumRecruit(this);
    }

    public void addPortfolios(List<Portfolio> portfolioList) {
        for (Portfolio portfolio : portfolioList) {
            this.addPortfolio(portfolio);
        }
    }

    public void updateFrom(RecruitRequestDTO.PremiumRecruitRequest request, String posterImageUrl) {
        this.title = request.getTitle();
        this.address = request.getAddress();
        if (request.getBusinessFields() != null) {
            this.businessFields = new java.util.HashSet<>(request.getBusinessFields());
        }
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.recruitStartDate = request.getRecruitStartDate();
        this.recruitEndDate = request.getRecruitEndDate();
        this.recruitCount=request.getRecruitCount();
        this.gender = request.getGender();
        this.education = request.getEducation();
        this.otherConditions = request.getOtherConditions();
        this.preferredConditions = request.getPreferredConditions();
        this.workDuration = request.getWorkDuration();
        this.workDurationOther = request.getWorkDurationOther();
        this.workTime = request.getWorkTime();
        this.workTimeOther = request.getWorkTimeOther();
        this.workDays = request.getWorkDays();
        this.workDaysOther = request.getWorkDaysOther();
        this.salary = request.getSalary();
        this.salaryType = request.getSalaryType();
        this.salaryOther = request.getSalaryOther();
        if (request.getApplicationMethods() != null) {
            this.applicationMethods = new java.util.HashSet<>(request.getApplicationMethods());
        }
        if (posterImageUrl != null) {
            this.posterImageUrl = posterImageUrl;
        }
    }

    public void updatePortfolios(java.util.List<RecruitRequestDTO.PremiumRecruitRequest.PortfolioRequest> portfolioDTOs) {
        this.portfolios.clear();
        if (portfolioDTOs != null) {
            for (RecruitRequestDTO.PremiumRecruitRequest.PortfolioRequest dto : portfolioDTOs) {
                Portfolio portfolio = Portfolio.builder()
                        .title(dto.getTitle())
                        .type(dto.getType())
                        .isRequired(dto.isRequired())
                        .maxFileCount(dto.getMaxFileCount())
                        .build();
                this.addPortfolio(portfolio);
            }
        }
    }
}
