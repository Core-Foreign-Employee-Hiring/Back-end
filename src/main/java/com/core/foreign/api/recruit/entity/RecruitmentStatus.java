package com.core.foreign.api.recruit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitmentStatus {
    PENDING("대기"),
    REJECTED("거절"),
    APPROVED("승인");

    private final String description;
}
