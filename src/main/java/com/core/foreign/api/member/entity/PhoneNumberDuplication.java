package com.core.foreign.api.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
public class PhoneNumberDuplication {
    @Id
    @GeneratedValue(strategy = IDENTITY)  // 이건 굳이...? phoneNumber 로 pk 잡을까?
    private Long id;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    public PhoneNumberDuplication(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
