package com.core.foreign.api.payment.entity;

import com.core.foreign.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey; // 토스 결제 고유 키

    @Column(nullable = false)
    private String orderId; // 프론트에서 생성한 주문 ID

    private String orderName; // 주문 이름(프리미엄 등록, 프리미엄 상단 광고, 일반 상단 광고)

    @Column(nullable = false)
    private String status; // 결제 상태 (READY, DONE, CANCELED 등)

    @Column(nullable = false)
    private Long totalAmount; // 총 결제 금액

    private Long balanceAmount; // 잔여 금액
    private Long suppliedAmount; // 공급가액
    private Long vat; // 부가세
    private String currency; // 결제 통화 (KRW 등)
    private String lastTransactionKey; // 마지막 트랜잭션 키

    private String method; // 결제 수단 (카드, 계좌이체 등)
    private LocalDateTime requestedAt; // 결제 요청 시간
    private LocalDateTime approvedAt; // 결제 승인 시간
    private LocalDateTime canceledAt; // 결제 취소 시간
    private String receiptUrl; // 영수증 URL
    private String cancelReason; // 취소 사유

    // 카드 결제 정보
    private String cardNumber; // 카드번호 (마스킹된 형태)
    private String cardType; // 신용, 체크카드 여부
    private String cardOwnerType; // 개인, 법인 여부
    private String issuerCode; // 카드 발급사 코드
    private String acquirerCode; // 카드 매입사 코드
    private String approveNo; // 승인번호
    private Integer installmentPlanMonths; // 할부 개월수
    private Boolean isInterestFree; // 무이자 여부
    private Boolean useCardPoint; // 카드 포인트 사용 여부
    private String acquireStatus; // 매입 상태 (READY 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 결제를 진행한 회원 정보
}
