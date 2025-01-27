package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.recruit.entity.PortfolioType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class RecruitRequestDTO {

    @Getter
    @Builder
    public static class GeneralRecruitRequest {
        private String title;
        private Address address;
        private List<String> businessFields;
        private Double latitude;
        private Double longitude;
        private LocalDate recruitStartDate;
        private LocalDate recruitEndDate;
        private Integer recruitCount;
        private String gender;
        private String education;
        private String otherConditions;
        private List<String> preferredConditions;
        private String workDuration;
        private String workTime;
        private String workDays;
        private String workDaysOther;
        private String salary;
        private String salaryType;
        private List<String> applicationMethods;
    }

    @Getter
    @Builder
    public static class PremiumRecruitRequest {
        private String title;
        private Address address;
        private List<String> businessFields;
        private Double latitude;
        private Double longitude;
        private LocalDate recruitStartDate;
        private LocalDate recruitEndDate;
        private Integer recruitCount;
        private String gender;
        private String education;
        private String otherConditions;
        private List<String> preferredConditions;
        private String workDuration;
        private String workTime;
        private String workDays;
        private String workDaysOther;
        private String salary;
        private String salaryType;
        private List<String> applicationMethods;

        private List<PortfolioRequest> portfolios;

        @Getter
        @Builder
        public static class PortfolioRequest {
            private String title;
            private PortfolioType type;
            private boolean isRequired;
            private Integer maxFileCount;
            private Long maxFileSize;
        }
    }
}
