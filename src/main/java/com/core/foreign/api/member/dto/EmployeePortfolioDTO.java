package com.core.foreign.api.member.dto;

import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldType;
import com.core.foreign.api.member.entity.Topic;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EmployeePortfolioDTO {
    private String introduction; // 자기소개

    private String enrollmentCertificateUrl; // 재학증명서
    private String transcriptUrl; // 성적증명서
    private String partTimeWorkPermitUrl; // 시간제근로허가서

    private Topic topic;
    private String englishTestType;
    private int englishTestScore; // 점수


    private List<EmployeePortfolioExperienceDTO> experiences;
    private List<EmployeePortfolioCertificationDTO> certifications;
    private List<EmployeePortfolioAwardDTO> awards;


    public static EmployeePortfolioDTO from(EmployeePortfolio portfolio) {
        EmployeePortfolioDTO dto = new EmployeePortfolioDTO();
        dto.introduction = portfolio.getIntroduction();
        dto.enrollmentCertificateUrl = portfolio.getEnrollmentCertificateUrl();
        dto.transcriptUrl = portfolio.getTranscriptUrl();
        dto.partTimeWorkPermitUrl = portfolio.getPartTimeWorkPermitUrl();
        dto.topic = portfolio.getTopic();
        dto.englishTestType = portfolio.getEnglishTestType();
        dto.englishTestScore = portfolio.getEnglishTestScore();


        List<EmployeePortfolioExperienceDTO> e = new ArrayList<>();
        List<EmployeePortfolioCertificationDTO> c = new ArrayList<>();
        List<EmployeePortfolioAwardDTO> a = new ArrayList<>();

        List<EmployeePortfolioBusinessFieldInfo> infos = portfolio.getEmployeePortfolioBusinessFieldInfos();
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
        return dto;

    }
}
