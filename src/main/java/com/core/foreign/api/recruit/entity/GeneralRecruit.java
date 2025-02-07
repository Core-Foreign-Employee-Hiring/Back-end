package com.core.foreign.api.recruit.entity;

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
            Set<String> businessFields,
            Double latitude,
            Double longitude,
            LocalDate recruitStartDate,
            LocalDate recruitEndDate,
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
                RecruitType.GENERAL,
                posterImageUrl,
                recruitPublishStatus
        );
    }

}
