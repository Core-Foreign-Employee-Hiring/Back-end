package com.core.foreign.api.contract.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum ContractStatus {
    NOT_COMPLETED("미완료"),   // 계약서 전체 상태
    COMPLETED("완료"),         // 계약서 전체 상태
    DRAFT_WRITING("작성 대기"),       // STANDARD, AGRICULTURE 전용
    COMPLETED_WRITING("작성 완료"),   // STANDARD, AGRICULTURE 전용
    PENDING_APPROVAL("승인 대기 중"), // IMAGE_UPLOAD 전용
    APPROVED("승인 완료"),    // IMAGE_UPLOAD 전용
    REJECTED("승인 반려"),    // IMAGE_UPLOAD 전용
    NONE("-");

    private final String description;

    // 계약서 형태에 따라 가능한 상태 리스트 반환
    public static List<ContractStatus> getAllowedStatuses(ContractType type) {
        return switch (type) {
            case STANDARD, AGRICULTURE -> List.of(DRAFT_WRITING, COMPLETED_WRITING);
            case IMAGE_UPLOAD -> List.of(PENDING_APPROVAL, APPROVED, REJECTED);
            case UNKNOWN -> List.of();
        };
    }
}
