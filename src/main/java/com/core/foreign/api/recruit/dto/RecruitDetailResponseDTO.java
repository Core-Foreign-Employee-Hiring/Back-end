package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.recruit.entity.ApplyMethod;
import com.core.foreign.api.recruit.entity.RecruitType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class RecruitDetailResponseDTO {
    private Long recruitId;                  // 공고 ID
    private String companyName;              // 회사(점포) 명
    private String companyIconImage;         // 회사 아이콘 이미지 URL
    private String title;                    // 공고 제목
    private Address address;                 // 공고 등록 시 사용된 주소
    private LocalDate recruitStartDate;      // 모집 시작일
    private LocalDate recruitEndDate;        // 모집 종료일
    private Integer recruitCount;            // 모집 인원
    private String gender;                   // 성별 조건
    private String education;                // 학력 조건
    private String otherConditions;          // 기타 조건
    private List<String> preferredConditions;// 우대 조건 리스트
    private Set<BusinessField> businessFields;      // 업직종 리스트
    private Set<ApplyMethod> applicationMethods;  // 지원 방법 리스트
    private List<String> workDuration;      // 근무 기간 목록
    private String workDurationOther;      // 근무 기간 기타 사항
    private List<String> workTime;          // 근무 시간 목록
    private String workTimeOther;          // 근무 시간 기타 사항
    private List<String> workDays;           // 근무 요일
    private String workDaysOther;            // 근무 요일 기타 사항
    private String salary;                   // 급여 정보 (예: 시급)
    private String salaryType;               // 급여 형태 (월급, 시급 등)
    private String salaryOther;            // 급여 기타 사항
    private Double latitude;                 // 위도
    private Double longitude;                // 경도
    private String posterImageUrl;           // 포스터 이미지 URL
    private RecruitType recruitType;         // 공고 유형 (GENERAL, PREMIUM)
    private Address employerAddress;         // 회사 주소
    private String employerContact;          // 연락처 (주요 전화번호)
    private String representative;           // 대표자 (고용주의 name 필드)
    private String employerEmail;            // 회사 이메일
    private String businessRegistrationNumber; // 사업자 등록번호
}