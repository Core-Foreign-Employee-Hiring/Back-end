package com.core.foreign.api.member.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum EvaluationCategory {
    // 고용인이 피고용인을 평가하는 항목
    WORKS_DILIGENTLY("성실하게 일해요.", EvaluationType.EMPLOYER_TO_EMPLOYEE),
    NO_LATENESS_OR_ABSENCE("지각/결근하지 않았어요.", EvaluationType.EMPLOYER_TO_EMPLOYEE),
    POLITE_AND_FRIENDLY("예의 바르고 친절해요.", EvaluationType.EMPLOYER_TO_EMPLOYEE),
    GOOD_CUSTOMER_SERVICE("고객 응대를 잘해요.", EvaluationType.EMPLOYER_TO_EMPLOYEE),
    SKILLED_AT_WORK("업무 능력이 좋아요.", EvaluationType.EMPLOYER_TO_EMPLOYEE),

    // 피고용인이 고용인을 평가하는 항목
    PAYS_ON_TIME("약속된 급여를 제때 줘요.", EvaluationType.EMPLOYEE_TO_EMPLOYER),
    KEEPS_CONTRACT_DATES("계약된 날짜를 잘 지켰어요.", EvaluationType.EMPLOYEE_TO_EMPLOYER),
    RESPECTS_EMPLOYEES("알바생을 존중해줘요.", EvaluationType.EMPLOYEE_TO_EMPLOYER),
    FRIENDLY_BOSS("사장님이 친절해요.", EvaluationType.EMPLOYEE_TO_EMPLOYER),
    FAIR_WORKLOAD("업무 강도가 적당해요.", EvaluationType.EMPLOYEE_TO_EMPLOYER);

    private final String description;
    private final EvaluationType type;

    EvaluationCategory(String description, EvaluationType type) {
        this.description = description;
        this.type = type;
    }

    // 특정 타입의 EvaluationCategory 리스트를 반환하는 정적 메서드
    public static List<EvaluationCategory> getByType(EvaluationType type) {
        return Arrays.stream(values()) // 모든 enum 값에 대해 스트림 생성
                .filter(category -> category.type == type) // 주어진 타입과 일치하는 값 필터링
                .collect(Collectors.toList()); // 리스트로 변환하여 반환
    }

    // description 으로 EvaluationCategory 반환
    public static EvaluationCategory getByDescription(String description) {
        return Arrays.stream(values()) // 모든 enum 값에 대해 스트림 생성
                .filter(category -> category.description.equals(description)) // description 이 일치하는 값 필터링
                .findFirst() // 첫 번째로 일치하는 항목을 찾음
                .orElseThrow(() -> new IllegalArgumentException("해당 description에 해당하는 항목이 없습니다.")); // 없으면 예외 발생
    }
}

