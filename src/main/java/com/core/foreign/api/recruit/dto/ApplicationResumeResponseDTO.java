package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.member.dto.EmployeePortfolioAwardDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioCertificationDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioExperienceDTO;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Topic;
import com.core.foreign.api.recruit.dto.internal.ResumeDTO;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ApplicationResumeResponseDTO {
    private Long resumeId;
    private Long employeeId;

    private String name;
    private String nationality;
    private String education;
    private String visa;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String zipcode;
    private String address1;
    private String address2;

    private String introduction; // 자기소개
    private String enrollmentCertificateUrl; // 재학증명서
    private String transcriptUrl; // 성적증명서
    private String partTimeWorkPermitUrl; // 시간제근로허가서
    private Topic topic;
    private List<EmployeePortfolioExperienceDTO> experiences;
    private List<EmployeePortfolioCertificationDTO> certifications;
    private List<EmployeePortfolioAwardDTO> awards;

    private String messageToEmployer;

    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;

    private RecruitmentStatus recruitmentStatus;
    private ContractStatus contractStatus;
    private EvaluationStatus isEmployeeEvaluatedByEmployer; // 고용인이 피고용인을 평가했는지 여부

    public static ApplicationResumeResponseDTO of(ResumeDTO resume, Employee employee, EmployeePortfolioDTO employeePortfolioDTO) {
        ApplicationResumeResponseDTO dto = new ApplicationResumeResponseDTO();

        Address address = employee.getAddress();

        dto.resumeId = resume.getResumeId();
        dto.employeeId = employee.getId();
        dto.name = employee.getName();
        dto.nationality = employee.getNationality();
        dto.education = employee.getEducation();
        dto.visa = employee.getVisa();
        dto.birthDate = employee.getBirthday();
        dto.email = employee.getEmail();
        dto.phoneNumber = employee.getPhoneNumber();
        dto.zipcode = address.getZipcode();
        dto.address1 = address.getAddress1();
        dto.address2 = address.getAddress2();
        dto.introduction = employeePortfolioDTO.getIntroduction();
        dto.enrollmentCertificateUrl = employeePortfolioDTO.getEnrollmentCertificateUrl();
        dto.transcriptUrl = employeePortfolioDTO.getTranscriptUrl();
        dto.partTimeWorkPermitUrl = employeePortfolioDTO.getPartTimeWorkPermitUrl();
        dto.topic = employeePortfolioDTO.getTopic();
        dto.experiences = employeePortfolioDTO.getExperiences();
        dto.certifications = employeePortfolioDTO.getCertifications();
        dto.awards = employeePortfolioDTO.getAwards();
        dto.messageToEmployer = resume.getMessageToEmployer();
        dto.texts = resume.getTexts();
        dto.files = resume.getFiles();
        dto.recruitmentStatus = resume.getRecruitmentStatus();
        dto.contractStatus = resume.getContractStatus();
        dto.isEmployeeEvaluatedByEmployer = resume.getIsEmployeeEvaluatedByEmployer();

        return dto;

    }

}
