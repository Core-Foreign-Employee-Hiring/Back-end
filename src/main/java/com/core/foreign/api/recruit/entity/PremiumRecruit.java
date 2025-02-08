package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
            Set<String> businessFields,
            Double latitude,
            Double longitude,
            LocalDate recruitStartDate,
            LocalDate recruitEndDate,
            Integer recruitCount,
            String gender,
            String education,
            String otherConditions,
            List<String> preferredConditions,
            String workDuration,
            String workTime,
            String workDays,
            String workDaysOther,
            String salary,
            String salaryType,
            Set<String> applicationMethods,
            String posterImageUrl,
            RecruitPublishStatus recruitPublishStatus
    ) {
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
                workTime,
                workDays,
                workDaysOther,
                salary,
                salaryType,
                applicationMethods,
                RecruitType.PREMIUM,
                posterImageUrl,
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
