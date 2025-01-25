package com.core.foreign.api.member.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("EMPLOYEE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee extends Member {

    private String nationality; // 국적
    private String education;   // 학력
    private String visa;        // 비자

    public Employee(String userId,
                    String password,
                    String name,
                    String email,
                    String phoneNumber,
                    Address address,
                    String nationality,
                    String education,
                    String visa,
                    LocalDate birthday,
                    boolean isMail,
                    boolean termsOfServiceAgreement,
                    boolean isOver15,
                    boolean personalInfoAgreement,
                    boolean adInfoAgreementSmsMms,
                    boolean adInfoAgreementEmail) {
        super(userId, password, name, email, phoneNumber, address, Role.EMPLOYEE, birthday, isMail, termsOfServiceAgreement,isOver15,  personalInfoAgreement, adInfoAgreementSmsMms, adInfoAgreementEmail);
        this.nationality = nationality;
        this.education = education;
        this.visa = visa;
    }
}
