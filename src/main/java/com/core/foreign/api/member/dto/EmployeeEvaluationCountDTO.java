package com.core.foreign.api.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeEvaluationCountDTO {
    private int worksDiligently;
    private int noLatenessOrAbsence;
    private int politeAndFriendly;
    private int goodCustomerService;
    private int skilledAtWork;
}
