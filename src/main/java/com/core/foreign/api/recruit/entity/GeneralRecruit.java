package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
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
                recruitPublishStatus
        );
    }

}
