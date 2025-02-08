package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.recruit.entity.ApplyMethod;
import com.core.foreign.api.recruit.entity.RecruitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitListResponseDTO {

    private Long recruitId;                    // 공고 ID
    private String companyName;                // 기업명
    private String title;                      // 모집제목
    private Address address;                   // 주소
    private List<String> workTime;            // 근무 시간 목록
    private List<String> workDays;             // 근무 요일 목록
    private List<String> workDuration;        // 근무기간
    private String salary;                     // 급여 정보 ("12000원")
    private String salaryType;                 // 급여 타입 (월급, 일급..)
    private Set<BusinessField> businessFields;      // 업직종(여러개)
    private String recruitPeriod;              // 모집기간
    private Set<ApplyMethod> applicationMethods;    // 접수방법
    private RecruitType recruitType;           // GENERAL or PREMIUM
}
