package com.core.foreign.api.member.dto;

import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TagResponseDTO {
    private Long recruitId;
    private String title;
    private EvaluationStatus evaluationStatus;
    private LocalDate tagRegistrationDate;


    public static TagResponseDTO from(Resume resume) {
        TagResponseDTO dto = new TagResponseDTO();

        Recruit recruit = resume.getRecruit();

        dto.recruitId = recruit.getId();
        dto.title=recruit.getTitle();
        dto.evaluationStatus=resume.getIsEmployerEvaluatedByEmployee();
        dto.tagRegistrationDate = resume.getEmployerEvaluationDate();

        return dto;
    }
}
