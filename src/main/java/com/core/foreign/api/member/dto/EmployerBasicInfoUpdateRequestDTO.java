package com.core.foreign.api.member.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployerBasicInfoUpdateRequestDTO {
    private String name;
    private LocalDate birthday;
    private boolean male;
}
