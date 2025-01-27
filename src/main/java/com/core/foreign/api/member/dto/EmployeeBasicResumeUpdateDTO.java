package com.core.foreign.api.member.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeeBasicResumeUpdateDTO {
    private String education;
    private String visa;

    private String zipcode;
    private String address1;
    private String address2;
}
