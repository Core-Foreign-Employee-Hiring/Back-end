package com.core.foreign.api.member.dto;

import com.core.foreign.api.member.entity.Employer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerProfileResponseDTO {
    private String name;
    private LocalDate birthday;
    private boolean isMale;

    private String email;
    private String phoneNumber;

    private String zipcode;
    private String address1;
    private String address2;

    private boolean termsOfServiceAgreement;
    private boolean isOver15; // 만 15세 이상 확인
    private boolean personalInfoAgreement;
    private boolean adInfoAgreementSnsMms;
    private boolean adInfoAgreementEmail;

    public static EmployerProfileResponseDTO from(Employer employer){

        return EmployerProfileResponseDTO.builder()
                .name(employer.getName())
                .birthday(employer.getBirthday())
                .isMale(employer.isMale())
                .email(employer.getEmail())
                .phoneNumber(employer.getPhoneNumber())
                .zipcode(employer.getAddress().getZipcode())
                .address1(employer.getAddress().getAddress1())
                .address2(employer.getAddress().getAddress2())
                .termsOfServiceAgreement(employer.isTermsOfServiceAgreement())
                .isOver15(employer.isOver15())
                .personalInfoAgreement(employer.isPersonalInfoAgreement())
                .adInfoAgreementSnsMms(employer.isAdInfoAgreementSnsMms())
                .adInfoAgreementEmail(employer.isAdInfoAgreementEmail())
                .build();

    }

}
