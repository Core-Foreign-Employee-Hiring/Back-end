package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MyRecruitResponseDTO {
    private String title; // 공고 제목
    private LocalDate recruitStartDate; // 모집 시작일
    private LocalDate recruitEndDate; // 모집 종료일
    private String workDuration; // 근무 기간
    private String workDays; // 근무 요일
    private String workTime; // 근무 시간
    private RecruitType recruitType; // 공고 유형 (GENERAL, PREMIUM)
    private boolean isUp;


    public static MyRecruitResponseDTO from(Recruit recruit) {
        MyRecruitResponseDTO dto = new MyRecruitResponseDTO();

        dto.title = recruit.getTitle();
        dto.recruitStartDate=recruit.getRecruitStartDate();
        dto.recruitEndDate=recruit.getRecruitEndDate();
        dto.workDuration=recruit.getWorkDuration();
        dto.workDays=recruit.getWorkDays();
        dto.workTime=recruit.getWorkTime();
        dto.recruitType = recruit.getRecruitType();
        dto.isUp=false;

        return dto;


    }
}
