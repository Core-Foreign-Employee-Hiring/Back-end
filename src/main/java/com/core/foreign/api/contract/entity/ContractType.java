package com.core.foreign.api.contract.entity;

public enum ContractType {
    STANDARD("표준근로계약서"),
    AGRICULTURE("표준근로계약서(농업·축산업·어업 분야)"),
    IMAGE_UPLOAD("파일(이미지) 업로드(실물 근로계약서)"),
    UNKNOWN("형태 미정");

    private final String description;

    ContractType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
