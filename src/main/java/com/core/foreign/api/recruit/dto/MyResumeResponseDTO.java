package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.dto.EmployeePortfolioAwardDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioCertificationDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioExperienceDTO;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Topic;
import com.core.foreign.api.recruit.dto.internal.ResumeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyResumeResponseDTO {
    private Long resumeId;

    private String name;
    private String nationality;
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

    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;

    private boolean portfolioPublic;


    public static MyResumeResponseDTO of(Employee employee, EmployeePortfolioDTO employeePortfolio, ResumeDTO resumeDTO) {
        MyResumeResponseDTO dto = new MyResumeResponseDTO();

        Address address = employee.getAddress();

        dto.resumeId = resumeDTO.getResumeId();
        dto.name = employee.getName();
        dto.nationality = employee.getNationality();
        dto.birthDate = employee.getBirthday();
        dto.email = employee.getEmail();
        dto.phoneNumber = employee.getPhoneNumber();
        dto.zipcode = address.getZipcode();
        dto.address1= address.getAddress1();
        dto.address2 = address.getAddress2();

        if(employeePortfolio!=null){
            dto.introduction=employeePortfolio.getIntroduction();
            dto.enrollmentCertificateUrl = employeePortfolio.getEnrollmentCertificateUrl();
            dto.transcriptUrl = employeePortfolio.getTranscriptUrl();
            dto.partTimeWorkPermitUrl=employeePortfolio.getPartTimeWorkPermitUrl();
            dto.topic = employeePortfolio.getTopic();
            dto.experiences=employeePortfolio.getExperiences();
            dto.certifications=employeePortfolio.getCertifications();
            dto.awards=employeePortfolio.getAwards();
        }

        dto.texts = resumeDTO.getTexts();
        dto.files = resumeDTO.getFiles();

        dto.portfolioPublic=resumeDTO.isPublic();

        return dto;

    }
}
