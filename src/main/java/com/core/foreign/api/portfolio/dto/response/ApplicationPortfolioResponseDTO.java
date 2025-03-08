package com.core.foreign.api.portfolio.dto.response;

import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.portfolio.dto.internal.BasicPortfolioDTO;
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

    public ApplicationPortfolioResponseDTO(Long resumeId, BasicPortfolioDTO basicPortfolioDTO,
                                           List<ResumePortfolioTextResponseDTO> texts, List<ResumePortfolioFileResponseDTO> files,
                                           Integer viewCount,EmployeeEvaluationCountDTO employeeEvaluation) {
        this.resumeId = resumeId;
        this.employeeId=basicPortfolioDTO.getEmployeeId();
        this.name=basicPortfolioDTO.getName();
        this.nationality=basicPortfolioDTO.getNationality();
        this.education=basicPortfolioDTO.getEducation();
        this.visa=basicPortfolioDTO.getVisa();
        this.birthday=basicPortfolioDTO.getBirthday();
        this.email=basicPortfolioDTO.getEmail();
        this.employeeEvaluationCountDTO=employeeEvaluation;
        this.employeePortfolioDTO = EmployeePortfolioDTO.from(basicPortfolioDTO);
        this.texts = texts;
        this.files = files;
        this.viewCount = viewCount;
        this.isLiked = false;

    }

    public void like(){
        this.isLiked=true;
    }
}
