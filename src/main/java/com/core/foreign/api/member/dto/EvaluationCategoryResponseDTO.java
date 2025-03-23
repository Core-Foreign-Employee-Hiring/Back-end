package com.core.foreign.api.member.dto;

import com.core.foreign.api.member.entity.EvaluationCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EvaluationCategoryResponseDTO {
    private Long resumeId;
    private List<EvaluationCategory> evaluationCategories;

}
