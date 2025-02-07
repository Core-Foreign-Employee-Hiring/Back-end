package com.core.foreign.api.recruit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class RecruitQuota {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Integer recruitCount;
    private Integer currentRecruitCount;

    @OneToOne
    private Recruit recruit;


    @Version
    private Long version;

}
