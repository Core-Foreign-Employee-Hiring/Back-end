package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.Recruit;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class RecruitPreviewResponseDTO {
    private Long recruitId;
    private String title; // 공고 제목
    private LocalDate recruitStartDate; // 모집 시작일
    private LocalDate recruitEndDate; // 모집 종료일
    private List<String> workDuration; // 근무 기간
    private List<String> workDays; // 근무 요일
    private List<String> workTime; // 근무 시간
    

    public static RecruitPreviewResponseDTO from(Recruit recruit){
        RecruitPreviewResponseDTO dto = new RecruitPreviewResponseDTO();

        dto.recruitId = recruit.getId();
        dto.title = recruit.getTitle();
        dto.recruitStartDate = recruit.getRecruitStartDate();
        dto.recruitEndDate = recruit.getRecruitEndDate();
        dto.workDuration = recruit.getWorkDuration();
        dto.workDays = recruit.getWorkDays();
        dto.workTime = recruit.getWorkTime();

        return dto;

    }

}
