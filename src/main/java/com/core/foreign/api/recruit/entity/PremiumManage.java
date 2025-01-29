package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ErrorStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class PremiumManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int premiumCount;      // 프리미엄 공고 횟수
    private int premiumJumpCount;  // 프리미엄 점프 횟수
    private int normalJumpCount;   // 일반 점프 횟수

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false, unique = true)
    private Employer employer;

    // 생성자
    public PremiumManage(Employer employer) {
        this.employer = employer;
        this.premiumCount = 0;
        this.premiumJumpCount = 0;
        this.normalJumpCount = 0;
    }

    // 프리미엄 공고 등록 횟수 증가
    public PremiumManage increasePremiumCount() {
        return this.toBuilder()
                .premiumCount(this.premiumCount + 1)
                .build();
    }

    // 프리미엄 공고 등록 횟수 감소
    public PremiumManage decreasePremiumCount() {
        if (this.premiumCount == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }
        return this.toBuilder()
                .premiumCount(this.premiumCount - 1)
                .build();
    }
}
