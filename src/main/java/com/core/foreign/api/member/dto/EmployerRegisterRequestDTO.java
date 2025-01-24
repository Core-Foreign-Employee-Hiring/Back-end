package com.core.foreign.api.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class EmployerRegisterRequestDTO {

    private String userId;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    private String zipcode;
    private String address1;
    private String address2;

    private String businessRegistrationNumber;
    private String companyName;
    private LocalDate establishedDate;
    private String businessField;

    @Builder
    public EmployerRegisterRequestDTO(String userId, String email, String password, String name, String phoneNumber,
                                   String zipcode, String address1, String address2,
                                   String businessRegistrationNumber, String companyName,
                                   LocalDate establishedDate, String businessField) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address1 = address1;
        this.address2 = address2;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.companyName = companyName;
        this.establishedDate = establishedDate;
        this.businessField = businessField;
    }
}