package com.core.foreign.api.recruit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResumePortfolio {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private PortfolioType portfolioType;

    private Long recruitPortfolioId;
    private String title;
    private String content;   // 파일일 경우 url 입니다.

    @ManyToOne(fetch = LAZY)
    private Resume resume;
}
