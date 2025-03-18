package com.core.foreign.api.portfolio.dto.response;

import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.portfolio.dto.internal.BasicPortfolioDTO;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BasicPortfolioResponseDTO {
    private Long employeeId;
    private String name;
    private String nationality; // 국적
    private String education;   // 학력
    private String visa;        // 비자
    private LocalDate birthday;  // 생년월일
    private String email;        // 이메일
    private EmployeePortfolioDTO employeePortfolioDTO;
    private EmployeeEvaluationCountDTO employeeEvaluationCountDTO;
    private Integer viewCount;
    private boolean isLiked;


    public static BasicPortfolioResponseDTO from(Employee employee, EmployeePortfolio employeePortfolio, EmployeeEvaluationCountDTO employeeEvaluationCountDTO){
        BasicPortfolioResponseDTO dto = new BasicPortfolioResponseDTO();
        EmployeePortfolioDTO employeePortfolioDTO = employeePortfolio!=null?EmployeePortfolioDTO.from(employeePortfolio, employee.isPortfolioPublic()):EmployeePortfolioDTO.emptyPortfolio();

        dto.employeeId = employee.getId();
        dto.name = employee.getName();
        dto.nationality = employee.getNationality();
        dto.education = employee.getEducation();
        dto.visa = employee.getVisa();
        dto.birthday = employee.getBirthday();
        dto.email = employee.getEmail();
        dto.employeePortfolioDTO = employeePortfolioDTO;
        dto.employeeEvaluationCountDTO = employeeEvaluationCountDTO;
        dto.viewCount = employee.getViewCount()+1;

        dto.isLiked=false;

        return dto;
    }

    public static BasicPortfolioResponseDTO from(BasicPortfolioDTO basicPortfolio, EmployeeEvaluationCountDTO employeeEvaluationCountDTO){
        BasicPortfolioResponseDTO dto = new BasicPortfolioResponseDTO();
        EmployeePortfolioDTO employeePortfolioDTO = EmployeePortfolioDTO.from(basicPortfolio);

        dto.employeeId = basicPortfolio.getEmployeeId();
        dto.name = basicPortfolio.getName();
        dto.nationality = basicPortfolio.getNationality();
        dto.education = basicPortfolio.getEducation();
        dto.visa = basicPortfolio.getVisa();
        dto.birthday = basicPortfolio.getBirthday();
        dto.email = basicPortfolio.getEmail();
        dto.employeePortfolioDTO = employeePortfolioDTO;
        dto.employeeEvaluationCountDTO = employeeEvaluationCountDTO;

        dto.isLiked=false;

        return dto;
    }

    public void like(){
        isLiked=true;
    }

    public void setViewCount(Integer viewCount){
        this.viewCount=viewCount;
    }
}
