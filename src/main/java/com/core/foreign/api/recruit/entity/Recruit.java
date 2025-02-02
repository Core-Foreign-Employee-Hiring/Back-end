package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "recruit")
public abstract class Recruit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // 공고 ID

    protected String title; // 공고 제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    protected Member employer; // 고용주 정보

    protected Double latitude; // 근무지의 위도
    protected Double longitude; // 근무지의 경도

    protected LocalDate recruitStartDate; // 모집 시작일
    protected LocalDate recruitEndDate; // 모집 종료일

    protected Integer recruitCount; // 모집 인원
    protected String gender; // 지원자 성별 조건
    protected String education; // 학력 조건
    protected String otherConditions; // 기타 조건

    @Embedded
    protected Address address; // 주소

    @ElementCollection
    @CollectionTable(name = "recruit_business_fields", joinColumns = @JoinColumn(name = "recruit_id"))
    @Column(name = "business_field", length = 100)
    protected Set<String> businessFields; // 업직종 리스트

    @ElementCollection
    @CollectionTable(name = "recruit_preferred_conditions", joinColumns = @JoinColumn(name = "recruit_id"))
    @Column(name = "preferred_condition", length = 100)
    protected List<String> preferredConditions; // 우대 조건 리스트

    @ElementCollection
    @CollectionTable(name = "recruit_application_methods", joinColumns = @JoinColumn(name = "recruit_id"))
    @Column(name = "method", length = 50)
    protected Set<String> applicationMethods; // 지원 방법 리스트

    protected String workDuration; // 근무 기간
    protected String workTime; // 근무 시간
    protected String workDays; // 근무 요일
    protected String workDaysOther; // 근무 요일 기타 사항 (추가 조건)
    protected String salary; // 급여 정보
    protected String salaryType; // 급여 형태 (월급, 시급 등)
    protected String posterImageUrl; // 포스터 이미지 URL

    @Enumerated(EnumType.STRING)
    protected RecruitType recruitType; // 공고 유형 (GENERAL, PREMIUM)

    @Enumerated(EnumType.STRING)
    protected RecruitPublishStatus recruitPublishStatus; // 공고 상태 (PUBLISHED, DRAFT)

    protected Recruit(String title,
                      Member employer,
                      Address address,
                      Set<String> businessFields,
                      Double latitude,
                      Double longitude,
                      LocalDate recruitStartDate,
                      LocalDate recruitEndDate,
                      Integer recruitCount,
                      String gender,
                      String education,
                      String otherConditions,
                      List<String> preferredConditions,
                      String workDuration,
                      String workTime,
                      String workDays,
                      String workDaysOther,
                      String salary,
                      String salaryType,
                      Set<String> applicationMethods,
                      RecruitType recruitType,
                      String posterImageUrl,
                      RecruitPublishStatus recruitPublishStatus
    ) {
        this.title = title;
        this.employer = employer;
        this.address = address;
        this.businessFields = businessFields;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recruitStartDate = recruitStartDate;
        this.recruitEndDate = recruitEndDate;
        this.recruitCount = recruitCount;
        this.gender = gender;
        this.education = education;
        this.otherConditions = otherConditions;
        this.preferredConditions = preferredConditions;
        this.workDuration = workDuration;
        this.workTime = workTime;
        this.workDays = workDays;
        this.workDaysOther = workDaysOther;
        this.salary = salary;
        this.salaryType = salaryType;
        this.applicationMethods = applicationMethods;
        this.recruitType = recruitType;
        this.posterImageUrl = posterImageUrl;
        this.recruitPublishStatus = recruitPublishStatus;
    }
}
