package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Member;
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
}
