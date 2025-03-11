package com.core.foreign.api.member.dto;

import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldType;
import com.core.foreign.api.member.entity.Topic;
import com.core.foreign.api.portfolio.dto.internal.BasicPortfolioDTO;
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

    private boolean portfolioPublic;


    public static EmployeePortfolioDTO from(EmployeePortfolio portfolio, boolean isPublic) {
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
        dto.portfolioPublic = isPublic;
        return dto;

    }

    public static EmployeePortfolioDTO from(EmployeePortfolio portfolio) {
        if(portfolio == null) {return null;}

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

    public static EmployeePortfolioDTO from(BasicPortfolioDTO basicPortfolio){
        EmployeePortfolioDTO dto = new EmployeePortfolioDTO();
        dto.introduction = basicPortfolio.getIntroduction();
        dto.enrollmentCertificateUrl = basicPortfolio.getEnrollmentCertificateUrl();
        dto.transcriptUrl = basicPortfolio.getTranscriptUrl();
        dto.partTimeWorkPermitUrl = basicPortfolio.getPartTimeWorkPermitUrl();
        dto.topic = basicPortfolio.getTopic();
        dto.englishTestType = basicPortfolio.getEnglishTestType();
        dto.englishTestScore = basicPortfolio.getEnglishTestScore();

        dto.experiences=basicPortfolio.getExperiences();
        dto.certifications=basicPortfolio.getCertifications();
        dto.awards=basicPortfolio.getAwards();

        return dto;
    }



}
