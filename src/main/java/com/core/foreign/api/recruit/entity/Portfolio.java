package com.core.foreign.api.recruit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "portfolio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 질문 ID

    private String title; // 질문 제목

    @Enumerated(EnumType.STRING)
    private PortfolioType type; // 질문 유형 (장문형, 단답형, 파일 업로드)

    private boolean isRequired; // 필수 여부

    // 파일 업로드 관련 필드
    private Integer maxFileCount; // 최대 업로드 가능 파일 갯수
    private Long maxFileSize; // 바이트 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "premium_recruit_id", nullable = false)
    private PremiumRecruit premiumRecruit;

    @Builder
    public Portfolio(
            String title,
            PortfolioType type,
            boolean isRequired,
            Integer maxFileCount,
            Long maxFileSize
    ) {
        this.title = title;
        this.type = type;
        this.isRequired = isRequired;
        this.maxFileCount = maxFileCount;
        this.maxFileSize = maxFileSize;
    }

    public void assignPremiumRecruit(PremiumRecruit premiumRecruit) {
        this.premiumRecruit = premiumRecruit;
    }
}
