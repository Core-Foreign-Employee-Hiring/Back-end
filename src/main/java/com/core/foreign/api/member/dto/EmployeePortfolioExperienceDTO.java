package com.core.foreign.api.member.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeePortfolioExperienceDTO {
    private BusinessField businessField;
    private String experienceDescription;
    private LocalDate startDate  ;
    private LocalDate endDate;



    public static EmployeePortfolioExperienceDTO from(EmployeePortfolioBusinessFieldInfo info) {
        EmployeePortfolioExperienceDTO result = new EmployeePortfolioExperienceDTO();
        result.businessField=info.getBusinessField();
        result.experienceDescription =info.getContent();
        result.startDate=info.getStartDate();
        result.endDate=info.getEndDate();
        return result;
    }
}
