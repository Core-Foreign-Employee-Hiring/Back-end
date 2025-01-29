package com.core.foreign.api.member.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeePortfolioAwardDTO {
    private BusinessField businessField;
    private String awardName;
    private LocalDate awardDate;

    public static EmployeePortfolioAwardDTO from(EmployeePortfolioBusinessFieldInfo info) {
        EmployeePortfolioAwardDTO result = new EmployeePortfolioAwardDTO();
        result.businessField=info.getBusinessField();
        result.awardName =info.getContent();
        result.awardDate=info.getStartDate();
        return result;
    }

}
