package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.Recruit;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyDraftRecruitResponseDTO {
    private Long recruitId; // 공고 ID
    private String title; // 공고 제목
    private LocalDateTime createdAt;  // 생성 날짜
    private LocalDateTime updatedAt;  // 수정 날짜



    public static MyDraftRecruitResponseDTO from(Recruit recruit){
        MyDraftRecruitResponseDTO dto = new MyDraftRecruitResponseDTO();

        dto.recruitId=recruit.getId();
        dto.title=recruit.getTitle();
        dto.createdAt=recruit.getCreatedAt();
        dto.updatedAt=recruit.getUpdatedAt();
        return dto;
    }
}
