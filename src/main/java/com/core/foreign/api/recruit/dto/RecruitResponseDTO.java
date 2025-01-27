package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.recruit.entity.PortfolioType;
import com.core.foreign.api.recruit.entity.RecruitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class RecruitResponseDTO {
    private Long id;                       // 공고 ID
    private Address address;               // 주소
    private String title;                  // 공고 제목
    private RecruitType recruitType;       // 공고 유형 (GENERAL, PREMIUM 등)
    private LocalDate recruitStartDate;    // 모집 시작일
    private LocalDate recruitEndDate;      // 모집 종료일
    private Integer recruitCount;          // 모집 인원
    private String gender;                 // 성별
    private String education;              // 학력 조건
    private String otherConditions;        // 기타 조건
    private List<String> preferredConditions; // 우대 조건 리스트
    private String workDuration;           // 근무 기간
    private String workTime;               // 근무 시간
    private String workDays;               // 근무 요일
    private String workDaysOther;          // 근무 요일 기타 사항
    private String salary;                 // 급여 정보
    private String salaryType;             // 급여 형태 (월급, 시급 등)
    private List<String> businessFields;   // 업직종 리스트
    private List<String> applicationMethods; // 지원 방법
    private String posterImageUrl;         // 포스터 이미지 URL
    private Double latitude;               // 위도
    private Double longitude;              // 경도
    private List<PortfolioDTO> portfolios; // 포트폴리오 데이터 (프리미엄 공고만)

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PortfolioDTO {
        private String title;              // 포트폴리오 제목
        private PortfolioType type;        // 포트폴리오 유형 (장문형, 단답형, 파일 업로드)
        private boolean isRequired;        // 필수 여부
        private Integer maxFileCount;      // 최대 파일 개수
        private Long maxFileSize;          // 최대 파일 크기
    }
}
