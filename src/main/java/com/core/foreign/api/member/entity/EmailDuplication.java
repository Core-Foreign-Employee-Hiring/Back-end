package com.core.foreign.api.member.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
public class EmailDuplication {
    @Id
    @GeneratedValue(strategy = IDENTITY)  // 이건 굳이...? email 로 pk 잡을까?
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    public EmailDuplication(String email) {
        this.email = email;
    }
}
