package com.core.foreign.api.recruit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplyMethod {
    ONLINE("온라인지원");

    private final String description;
}
