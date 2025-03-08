package com.core.foreign.api.portfolio.dto.response;


import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.entity.Employee;
import lombok.Getter;

@Getter
public class BasicPortfolioPreviewResponseDTO {
    private Long employeeId;
    private String name;
    private EmployeeEvaluationCountDTO employeeEvaluationCount;

    public BasicPortfolioPreviewResponseDTO(Employee employee, EmployeeEvaluationCountDTO employeeEvaluationCount) {
        this.employeeId = employee.getId();
        this.name = employee.getName();
        this.employeeEvaluationCount = employeeEvaluationCount;
    }
}
