package com.core.foreign.api.contract.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.ApplyMethod;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class EmployerCompletedContractResponseDTO {
    private Long recruitId;
    private Long contractId;
    private String title; // 공고 제목
    private List<String> workDuration; // 근무 기간 목록
    private List<String> workDays;      // 근무 요일 목록
    private String zipcode;  // 우편번호
    private String address1; // 주소
    private String address2; // 상세 주소
    private List<String> workTime; // 근무 시간
    private LocalDate recruitStartDate; // 모집 시작일
    private LocalDate recruitEndDate; // 모집 종료일
    private List<BusinessField> businessFields;
    private String salary; // 급여 정보
    private String salaryType;
    private List<ApplyMethod> applyMethods;
    private String employeeName;

    public static EmployerCompletedContractResponseDTO from(ContractMetadata contractMetadata){
        EmployerCompletedContractResponseDTO dto = new EmployerCompletedContractResponseDTO();

        Resume resume = contractMetadata.getResume();
        Recruit recruit = resume.getRecruit();
        Employee employee = resume.getEmployee();
        Address address = recruit.getAddress();

        dto.recruitId = resume.getId();
        dto.contractId = contractMetadata.getId();
        dto.title= recruit.getTitle();
        dto.recruitStartDate = recruit.getRecruitStartDate();
        dto.recruitEndDate = recruit.getRecruitEndDate();
        dto.employeeName = employee.getName();
        dto.workDuration = null;
        dto.workDays = null;
        dto.workTime = null;
        dto.businessFields = null;
        dto.zipcode=address.getZipcode();
        dto.address1=address.getAddress1();
        dto.address2=address.getAddress2();
        dto.salaryType= recruit.getSalaryType();
        dto.salary=recruit.getSalary();
        dto.applyMethods=null;

        return dto;

    }

}
