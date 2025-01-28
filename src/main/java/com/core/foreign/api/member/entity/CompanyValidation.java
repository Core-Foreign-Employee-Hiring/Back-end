package com.core.foreign.api.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
public class CompanyValidation {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String businessNo;
    private String startDate;
    private String representativeName;


    public CompanyValidation(String businessNo, String startDate, String representativeName) {
        this.businessNo = businessNo;
        this.startDate = startDate;
        this.representativeName = representativeName;
    }
}
