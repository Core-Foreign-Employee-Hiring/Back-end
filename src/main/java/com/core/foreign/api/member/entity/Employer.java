package com.core.foreign.api.member.entity;

import com.core.foreign.api.recruit.entity.PremiumManage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@DiscriminatorValue("EMPLOYER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employer extends Member {

    private String companyImageUrl;

    private String businessRegistrationNumber; // 사업자등록번호
    private String archivedBusinessRegistrationNumber;
    private String companyName;                // 회사점포명
    private String archivedCompanyName;
    private LocalDate establishedDate;         // 설립일
    private LocalDate archivedEstablishedDate;

    private String companyEmail;
    private String archivedCompanyEmail;
    private String mainPhoneNumber;
    private String archivedMainPhoneNumber;

    @Embedded
    private Address companyAddress;

    @OneToOne(mappedBy = "employer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private PremiumManage premiumManage;

    public Employer(String userId,
                    String password,
                    String name,
                    String email,
                    String phoneNumber,
                    Address address,
                    String businessRegistrationNumber,
                    String companyName,
                    LocalDate establishedDate,
                    LocalDate birthday,
                    boolean isMale,
                    boolean termsOfServiceAgreement,
                    boolean isOver15,
                    boolean personalInfoAgreement,
                    boolean adInfoAgreementSmsMms,
                    boolean adInfoAgreementEmail) {
        super(userId, password, name, email, phoneNumber, address, Role.EMPLOYER, birthday, isMale, termsOfServiceAgreement, isOver15, personalInfoAgreement, adInfoAgreementSmsMms, adInfoAgreementEmail);
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.companyName = companyName;
        this.establishedDate = establishedDate;
        this.companyEmail=email;
        this.mainPhoneNumber=phoneNumber;

        this.premiumManage = new PremiumManage(this);
        this.companyAddress=address;

        this.archivedBusinessRegistrationNumber=null;
        this.archivedCompanyName=null;
        this.archivedEstablishedDate=null;
        this.archivedCompanyEmail=null;
        this.archivedMainPhoneNumber=null;
    }

    public void updateCompanyEmail(String companyEmail){
        this.companyEmail=companyEmail;
    }

    public void updateCompanyMainPhoneNumber(String mainPhoneNumber){
        this.mainPhoneNumber=mainPhoneNumber;
    }

    public void updateCompanyAddress(Address address){
        this.companyAddress = address;
    }

    public void updateBusinessInfo(String businessNo, String startDate, String representativeName) {
        businessRegistrationNumber = businessNo;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        establishedDate = LocalDate.parse(startDate, formatter);
        updateName(representativeName);
    }


    public void withdraw(){
        this.archivedBusinessRegistrationNumber=this.businessRegistrationNumber;
        this.archivedCompanyName=this.companyName;
        this.archivedEstablishedDate=this.establishedDate;
        this.archivedCompanyEmail=this.companyEmail;
        this.archivedMainPhoneNumber=this.mainPhoneNumber;

        this.businessRegistrationNumber=null;
        this.companyName=null;
        this.establishedDate=null;
        this.companyEmail=null;
        this.mainPhoneNumber=null;

        super.withdraw();
    }

}
