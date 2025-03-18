package com.core.foreign.api.portfolio.dto.internal;

import com.core.foreign.api.member.dto.EmployeePortfolioAwardDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioCertificationDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioExperienceDTO;
import com.core.foreign.api.member.entity.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BasicPortfolioDTO {
    private Long employeeId;
    private String name;
    private String nationality; // 국적
    private String education;   // 학력
    private String visa;        // 비자
    private LocalDate birthday;  // 생년월일
    private String email;        // 이메일

    private String introduction; // 자기소개
    private String enrollmentCertificateUrl; // 재학증명서
    private String transcriptUrl; // 성적증명서
    private String partTimeWorkPermitUrl; // 시간제근로허가서
    private Topic topic;
    private List<EmployeePortfolioExperienceDTO> experiences;
    private List<EmployeePortfolioCertificationDTO> certifications;
    private List<EmployeePortfolioAwardDTO> awards;


    public static BasicPortfolioDTO from(Employee employee, EmployeePortfolio employeePortfolio){
        BasicPortfolioDTO dto = new BasicPortfolioDTO();

        dto.employeeId = employee.getId();
        dto.name = employee.getName();
        dto.nationality = employee.getNationality();
        dto.education = employee.getEducation();
        dto.visa = employee.getVisa();
        dto.birthday = employee.getBirthday();
        dto.email = employee.getEmail();

        if(employeePortfolio!=null){
            dto.introduction=employeePortfolio.getIntroduction();
            dto.enrollmentCertificateUrl = employeePortfolio.getEnrollmentCertificateUrl();
            dto.transcriptUrl = employeePortfolio.getTranscriptUrl();
            dto.partTimeWorkPermitUrl = employeePortfolio.getPartTimeWorkPermitUrl();
            dto.topic = employeePortfolio.getTopic();

            List<EmployeePortfolioExperienceDTO> e = new ArrayList<>();
            List<EmployeePortfolioCertificationDTO> c = new ArrayList<>();
            List<EmployeePortfolioAwardDTO> a = new ArrayList<>();

            List<EmployeePortfolioBusinessFieldInfo> infos = employeePortfolio.getEmployeePortfolioBusinessFieldInfos();
            for (EmployeePortfolioBusinessFieldInfo info : infos) {
                if (info.getEmployeePortfolioBusinessFieldType() == EmployeePortfolioBusinessFieldType.EXPERIENCE) {
                    e.add(EmployeePortfolioExperienceDTO.from(info));
                } else if (info.getEmployeePortfolioBusinessFieldType() == EmployeePortfolioBusinessFieldType.CERTIFICATION) {
                    c.add(EmployeePortfolioCertificationDTO.from(info));
                } else if (info.getEmployeePortfolioBusinessFieldType() == EmployeePortfolioBusinessFieldType.AWARD) {
                    a.add(EmployeePortfolioAwardDTO.from(info));
                }
            }

            dto.experiences = e;
            dto.certifications = c;
            dto.awards = a;
        }

        return dto;
    }
}
