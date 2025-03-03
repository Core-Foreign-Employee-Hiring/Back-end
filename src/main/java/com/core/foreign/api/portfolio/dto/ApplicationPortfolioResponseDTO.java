package com.core.foreign.api.portfolio.dto;

import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioFileResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioTextResponseDTO;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ApplicationPortfolioResponseDTO {
    private Long resumeId;
    private Long employeeId;
    private String name;
    private String nationality; // 국적
    private String education;   // 학력
    private String visa;        // 비자
    private LocalDate birthday;  // 생년월일
    private String email;        // 이메일
    private EmployeeEvaluationCountDTO employeeEvaluationCountDTO;
    private EmployeePortfolioDTO employeePortfolioDTO;
    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;
    private Integer viewCount;
    private boolean isLiked;


    public ApplicationPortfolioResponseDTO(Long resumeId, BasicPortfolioResponseDTO basicPortfolioResponseDTO,
                                           EmployeePortfolioDTO employeePortfolioDTO,
                                           List<ResumePortfolioTextResponseDTO> texts, List<ResumePortfolioFileResponseDTO> files,
                                           Integer viewCount) {
        this.resumeId = resumeId;
        this.employeeId=basicPortfolioResponseDTO.getEmployeeId();
        this.name=basicPortfolioResponseDTO.getName();
        this.nationality=basicPortfolioResponseDTO.getNationality();
        this.education=basicPortfolioResponseDTO.getEducation();
        this.visa=basicPortfolioResponseDTO.getVisa();
        this.birthday=basicPortfolioResponseDTO.getBirthday();
        this.email=basicPortfolioResponseDTO.getEmail();
        this.employeeEvaluationCountDTO=basicPortfolioResponseDTO.getEmployeeEvaluationCountDTO();
        this.employeePortfolioDTO = employeePortfolioDTO;
        this.texts = texts;
        this.files = files;
        this.viewCount = viewCount;
        this.isLiked = false;

    }

    public void like(){
        this.isLiked=true;
    }
}
