package com.core.foreign.api.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmployeeRegisterRequestDTO {

    private String userId;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    private String zipcode;
    private String address1;
    private String address2;

    private String nationality;
    private String education;
    private String visa;

    @Builder
    public EmployeeRegisterRequestDTO(String userId, String email, String password, String name, String phoneNumber,
                                   String zipcode, String address1, String address2,
                                   String nationality, String education, String visa) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address1 = address1;
        this.address2 = address2;
        this.nationality = nationality;
        this.education = education;
        this.visa = visa;
    }
}