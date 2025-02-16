package com.core.foreign.api.portfolio.dto;

import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
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


    public static BasicPortfolioResponseDTO from(Employee employee, EmployeePortfolio employeePortfolio, EmployeeEvaluationCountDTO employeeEvaluationCountDTO){
        BasicPortfolioResponseDTO dto = new BasicPortfolioResponseDTO();
        EmployeePortfolioDTO employeePortfolioDTO = employeePortfolio!=null?EmployeePortfolioDTO.from(employeePortfolio, employee.isPortfolioPublic()):null;

        dto.employeeId = employee.getId();
        dto.name = employee.getName();
        dto.nationality = employee.getNationality();
        dto.education = employee.getEducation();
        dto.visa = employee.getVisa();
        dto.birthday = employee.getBirthday();
        dto.email = employee.getEmail();
        dto.employeePortfolioDTO = employeePortfolioDTO;
        dto.employeeEvaluationCountDTO = employeeEvaluationCountDTO;
        return dto;

    }
}
