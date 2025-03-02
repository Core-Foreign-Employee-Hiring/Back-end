package com.core.foreign.api.member.entity;

import com.core.foreign.api.member.dto.EmployeeBasicResumeUpdateDTO;
import jakarta.persistence.Column;
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

    @Column(name = "is_portfolio_public")
    private boolean isPortfolioPublic;

    private Integer viewCount;

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
        this.isPortfolioPublic = false;
        this.viewCount = 0;
    }


    public void updateBasicResume(EmployeeBasicResumeUpdateDTO updateDTO) {
        education = updateDTO.getEducation();
        visa = updateDTO.getVisa();
        updateAddress(new Address(updateDTO.getZipcode(), updateDTO.getAddress1(), updateDTO.getAddress2()));
    }

    public void publicizePortfolio() {
        this.isPortfolioPublic = true;
    }

    public void privatizePortfolio() {
        this.isPortfolioPublic = false;
    }

}
