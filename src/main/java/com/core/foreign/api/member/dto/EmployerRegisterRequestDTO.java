package com.core.foreign.api.member.dto;

import com.core.foreign.api.business_field.BusinessField;
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

    private LocalDate birthDate;
    private boolean isMale;

    private String zipcode;
    private String address1;
    private String address2;

    private String businessRegistrationNumber;
    private String companyName;
    private LocalDate establishedDate;
    private String representativeName;
    private BusinessField businessField;   //

    private boolean termsOfServiceAgreement;
    private boolean isOver15; // 만 15세 이상 확인
    private boolean personalInfoAgreement;
    private boolean adInfoAgreementSnsMms;
    private boolean adInfoAgreementEmail;

    @Builder
    public EmployerRegisterRequestDTO(String userId, String email, String password, String name, String phoneNumber,
                                      String zipcode, String address1, String address2,
                                      LocalDate birthDate, boolean isMale,
                                      String businessRegistrationNumber, String companyName,
                                      LocalDate establishedDate, BusinessField businessField,
                                      boolean termsOfServiceAgreement, boolean isOver15,
                                      boolean personalInfoAgreement, boolean adInfoAgreementSnsMms, boolean adInfoAgreementEmail) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.address1 = address1;
        this.address2 = address2;
        this.birthDate = birthDate;
        this.isMale = isMale;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.companyName = companyName;
        this.establishedDate = establishedDate;
        this.businessField = businessField;
        this.termsOfServiceAgreement = termsOfServiceAgreement;
        this.isOver15 = isOver15;
        this.personalInfoAgreement = personalInfoAgreement;
        this.adInfoAgreementSnsMms = adInfoAgreementSnsMms;
        this.adInfoAgreementEmail = adInfoAgreementEmail;
    }
}