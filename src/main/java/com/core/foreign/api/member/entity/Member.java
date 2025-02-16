package com.core.foreign.api.member.entity;

import com.core.foreign.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "member_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // 엔티티 식별번호

    @Column(unique = true)
    private String userId;       // 아이디
    private String password;     // 비밀번호
    private String name;         // 이름[대표자명]
    @Column(unique = true)
    private String email;        // 이메일
    @Column(unique = true)
    private String phoneNumber;  // 전화번호
    private String refreshToken; // 리프레시토큰
    private LocalDate birthday;  // 생년월일
    private boolean isMale;      // 성별

    private boolean termsOfServiceAgreement;
    private boolean isOver15; // 만 15세 이상 확인
    private boolean personalInfoAgreement;
    private boolean adInfoAgreementSnsMms;
    private boolean adInfoAgreementEmail;


    @Enumerated(EnumType.STRING)
    private Role role;           // 회원 Role (EMPLOYEE, EMPLOYER 등)

    @Embedded
    private Address address;     // 주소[회사/점포 주소]

    private int evaluationJoinCount;
    private LocalDateTime passwordVerifiedAt;

    protected Member(String userId,
                     String password,
                     String name,
                     String email,
                     String phoneNumber,
                     Address address,
                     Role role,
                     LocalDate birthday,
                     boolean isMale,
                     boolean termsOfServiceAgreement,
                     boolean isOver15,
                     boolean personalInfoAgreement,
                     boolean adInfoAgreementSmsMms,
                     boolean adInfoAgreementEmail) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.birthday = birthday;
        this.isMale = isMale;
        this.termsOfServiceAgreement = termsOfServiceAgreement;
        this.isOver15 = isOver15;
        this.personalInfoAgreement = personalInfoAgreement;
        this.adInfoAgreementSnsMms = adInfoAgreementSmsMms;
        this.adInfoAgreementEmail = adInfoAgreementEmail;
        this.evaluationJoinCount = 0;

    }

    // 리프레시토큰 업데이트
    public Member updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }


    public void updateBasicInfo(String name, LocalDate birthday, boolean isMale){
        this.name = name;
        this.birthday = birthday;
        this.isMale = isMale;
    }

    public void updateAgreement(boolean termsOfServiceAgreement,
                                boolean isOver15,
                                boolean personalInfoAgreement,
                                boolean adInfoAgreementSmsMms,
                                boolean adInfoAgreementEmail){
        this.termsOfServiceAgreement = termsOfServiceAgreement;
        this.isOver15=isOver15;
        this.personalInfoAgreement = personalInfoAgreement;
        this.adInfoAgreementSnsMms = adInfoAgreementSmsMms;
        this.adInfoAgreementEmail = adInfoAgreementEmail;
    }

    public void updateEmail(String email){
        this.email=email;
    }

    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }

    public void updateAddress(Address address){
        this.address=address;
    }


    public void updatePassword(String password){
        this.password=password;
    }


    public void updateName(String name){
        this.name=name;
    }

    public void updatePasswordVerifiedAt(){
        this.passwordVerifiedAt=LocalDateTime.now();
    }

    public void resetPasswordVerificationTime() {
        this.passwordVerifiedAt = LocalDateTime.now().minusDays(1);
    }

}
