package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.Recruit;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class RecruitmentApplyStatusDTO {
    private Long recruitId;
    private String title; // 공고 제목
    private LocalDate recruitStartDate; // 모집 시작일
    private LocalDate recruitEndDate; // 모집 종료일
    private List<String> workDuration; // 근무 기간 목록
    private List<String> workDays;      // 근무 요일 목록
    private List<String> workTime; // 근무 시간
    private Long resumeCount;


    public static RecruitmentApplyStatusDTO from(Recruit recruit, Long resumeCount){
        RecruitmentApplyStatusDTO dto = new RecruitmentApplyStatusDTO();

        dto.recruitId = recruit.getId();
        dto.title = recruit.getTitle();
        dto.recruitStartDate=recruit.getRecruitStartDate();
        dto.recruitEndDate=recruit.getRecruitEndDate();
       /* dto.workDuration = recruit.getWorkDuration();
        dto.workDays = recruit.getWorkDays();
        dto.workTime = recruit.getWorkTime();*/

        dto.workDuration = null;
        dto.workDays = null;
        dto.workTime = null;

        dto.resumeCount = (resumeCount==null)?0L: resumeCount;

        return dto;
    }
}
