package com.core.foreign.api.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployerEvaluationCountDTO {
    private int paysOnTime;
    private int keepsContractDates;
    private int respectsEmployees;
    private int friendlyBoss;
    private int fairWorkload;
    private int joinCount;
}
