package com.core.foreign.api.member.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeePortfolioCertificationDTO {
    private BusinessField businessField;
    private String certificateName;
    private LocalDate certificateDate ;

    public static EmployeePortfolioCertificationDTO from(EmployeePortfolioBusinessFieldInfo info) {
        EmployeePortfolioCertificationDTO result = new EmployeePortfolioCertificationDTO();
        result.businessField=info.getBusinessField();
        result.certificateName =info.getContent();
        result.certificateDate=info.getStartDate();
        return result;
    }
}
