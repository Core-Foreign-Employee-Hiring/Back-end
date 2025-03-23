package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitType;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MyRecruitResponseDTO {
    private Long recruitId;
    private String title; // 공고 제목
    private LocalDate recruitStartDate; // 모집 시작일
    private LocalDate recruitEndDate; // 모집 종료일
    private List<String> workDuration; // 근무 기간 목록
    private List<String> workDays;      // 근무 요일 목록
    private List<String> workTime; // 근무 시간
    private RecruitType recruitType; // 공고 유형 (GENERAL, PREMIUM)
    private String zipcode;  // 우편번호
    private String address1; // 주소
    private String address2; // 상세 주소


    public static MyRecruitResponseDTO from(Recruit recruit) {
        MyRecruitResponseDTO dto = new MyRecruitResponseDTO();
        Address address = recruit.getAddress();

        dto.recruitId=recruit.getId();
        dto.title = recruit.getTitle();
        dto.recruitStartDate=recruit.getRecruitStartDate();
        dto.recruitEndDate=recruit.getRecruitEndDate();
        dto.workDuration = recruit.getWorkDuration();
        dto.workDays = recruit.getWorkDays();
        dto.workTime = recruit.getWorkTime();
        dto.zipcode=address.getZipcode();
        dto.address1=address.getAddress1();
        dto.address2=address.getAddress2();
        dto.recruitType = recruit.getRecruitType();

        return dto;
    }
}
