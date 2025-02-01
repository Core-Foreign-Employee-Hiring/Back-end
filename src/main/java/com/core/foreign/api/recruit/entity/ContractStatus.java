package com.core.foreign.api.recruit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContractStatus {
    NOT_WRITTEN("미작성"),
    WRITTEN("작성");

    private final String description;
}
