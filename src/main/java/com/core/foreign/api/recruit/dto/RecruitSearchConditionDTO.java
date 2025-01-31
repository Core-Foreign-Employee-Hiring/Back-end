package com.core.foreign.api.recruit.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RecruitSearchConditionDTO {

    private Integer page;             // 0부터 시작
    private Integer size;             // 페이지 당 건수

    private java.util.List<String> businessFields;  // 업직종 (최대 N개)
    private java.util.List<String> workDurations;   // 근무기간 (최대 6개)
    private java.util.List<String> workDays;        // 근무요일 (최대 3개)
    private java.util.List<String> workTimes;       // 근무시간 (최대 3개)
    private String gender;                         // 성별 (남자, 여자, null=무관)
    private String salaryType;                     // 급여형태 (시급, 월급, 연봉 등)
}
