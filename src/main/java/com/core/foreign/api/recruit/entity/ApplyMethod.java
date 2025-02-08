package com.core.foreign.api.recruit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplyMethod {
    ONLINE("온라인지원"),
    INQUIRY("문의 지원"),
    VISIT("방문 접수"),
    CALL_VISIT("전화 후 방문");

    private final String description;
}
