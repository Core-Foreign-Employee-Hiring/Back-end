package com.core.foreign.api.member.dto;

import com.core.foreign.api.member.entity.EvaluationCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class EmployeeToEmployerEvaluationRequestDTO {
    private Long recruitId;
    private List<EvaluationCategory> evaluationCategory;
}
