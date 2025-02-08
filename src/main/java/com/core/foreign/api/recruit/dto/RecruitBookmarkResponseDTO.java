package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitBookmark;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecruitBookmarkResponseDTO {
    private Long recruitId;
    private String companyName;
    private LocalDate recruitEndDate; // 모집 종료일
    private boolean isEnd;

    public static RecruitBookmarkResponseDTO from(RecruitBookmark bookmark) {
        Recruit recruit = bookmark.getRecruit();

        RecruitBookmarkResponseDTO dto = new RecruitBookmarkResponseDTO();
        Employer employer = (Employer)recruit.getEmployer();

        dto.recruitId = recruit.getId();
        dto.companyName = employer.getCompanyName();
        dto.recruitEndDate=recruit.getRecruitEndDate();
        dto.isEnd= LocalDate.now().isAfter(recruit.getRecruitEndDate());

        return dto;
    }
}
