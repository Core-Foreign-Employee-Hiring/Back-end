package com.core.foreign.api.portfolio.dto.response;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicationPortfolioPreviewResponseDTO {
    private Long resumeId;
    private String name;
    private List<BusinessField> businessFields;
    private EmployeeEvaluationCountDTO employeeEvaluationCount;


    public ApplicationPortfolioPreviewResponseDTO(Resume resume, EmployeeEvaluationCountDTO employeeEvaluationCount, List<BusinessField> businessFields) {
        this.resumeId = resume.getId();
        this.name = resume.getEmployee().getName();
        this.employeeEvaluationCount = employeeEvaluationCount;
        this.businessFields = businessFields;
    }
}
