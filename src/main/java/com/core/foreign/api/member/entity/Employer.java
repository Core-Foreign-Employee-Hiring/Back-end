package com.core.foreign.api.member.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("EMPLOYER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employer extends Member {

    private String businessRegistrationNumber; // 사업자등록번호
    private String companyName;                // 회사점포명
    private LocalDate establishedDate;         // 설립일
    private String businessField;              // 업직종

    public Employer(String userId,
                    String password,
                    String name,
                    String email,
                    String phoneNumber,
                    Address address,
                    String businessRegistrationNumber,
                    String companyName,
                    LocalDate establishedDate,
                    String businessField) {
        super(userId, password, name, email, phoneNumber, address, Role.EMPLOYER);
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.companyName = companyName;
        this.establishedDate = establishedDate;
        this.businessField = businessField;
    }
}
