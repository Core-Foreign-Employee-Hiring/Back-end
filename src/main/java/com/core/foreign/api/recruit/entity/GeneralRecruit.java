package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.recruit.dto.RecruitRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "general_recruit")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeneralRecruit extends Recruit {

    @Builder(toBuilder = true)
    public GeneralRecruit(
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
                RecruitType.GENERAL,
                posterImageUrl,
                jumpDate,
                recruitPublishStatus
        );
    }

    public void updateFrom(RecruitRequestDTO.GeneralRecruitRequest request, String posterImageUrl) {
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

}
