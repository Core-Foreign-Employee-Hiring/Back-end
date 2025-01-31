package com.core.foreign.api.recruit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EvaluationStatus {
    COMPLETED("평가 완료"),
    NOT_EVALUATED("미평가"),
    NONE("-");

    private final String description;
}
