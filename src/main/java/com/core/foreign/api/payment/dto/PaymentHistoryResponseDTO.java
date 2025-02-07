package com.core.foreign.api.payment.dto;

import com.core.foreign.api.payment.entity.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentHistoryResponseDTO {
    private String orderName; // 주문 이름(프리미엄 등록, 프리미엄 상단 광고, 일반 상단
    private LocalDateTime approvedAt; // 결제 승인 시간
    private Long totalAmount; // 총 결제 금액
    private String status; // 결제 상태 (READY, DONE, CANCELED 등)


    public static PaymentHistoryResponseDTO from(Payment payment) {
        PaymentHistoryResponseDTO dto = new PaymentHistoryResponseDTO();
        dto.orderName = payment.getOrderName();
        dto.approvedAt = payment.getApprovedAt();
        dto.totalAmount = payment.getTotalAmount();
        dto.status = payment.getStatus();
        return dto;
    }
}
