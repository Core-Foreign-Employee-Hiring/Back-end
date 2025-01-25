package com.core.foreign.api.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private LocalDate birthDate;
    private boolean isMale;

    private String nationality;
    private String education;
    private String visa;

    private boolean termsOfServiceAgreement;
    private boolean isOver15; // 만 15세 이상 확인
    private boolean personalInfoAgreement;
    private boolean adInfoAgreementSnsMms;
    private boolean adInfoAgreementEmail;

    @Builder
    public EmployeeRegisterRequestDTO(String userId, String email, String password, String name, String phoneNumber,
                                      String zipcode, String address1, String address2,
                                      LocalDate birthDate, boolean isMale,
                                      String nationality, String education, String visa,
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
        this.nationality = nationality;
        this.education = education;
        this.visa = visa;
        this.termsOfServiceAgreement = termsOfServiceAgreement;  // 서비스 이용 약관 동의
        this.isOver15 = isOver15;                                // 만 15세 이상 확인
        this.personalInfoAgreement = personalInfoAgreement;      // 개인정보 수집 및 이용 동의
        this.adInfoAgreementSnsMms = adInfoAgreementSnsMms;      // 광고성 정보 수신 동의 (SNS/MMS)
        this.adInfoAgreementEmail = adInfoAgreementEmail;        // 광고성 정보 수신 동의 (이메일)
    }
}