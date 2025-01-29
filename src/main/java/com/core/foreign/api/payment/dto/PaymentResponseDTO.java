package com.core.foreign.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String orderId;      // 주문 ID
    private String orderName;    // 주문 이름
    private String status;       // 결제 상태 (READY, DONE, CANCELED 등)
    private Long totalAmount;    // 총 결제 금액
    private String method;       // 결제 수단
    private String requestedAt;  // 요청 시간
    private String approvedAt;   // 승인 시간
    private String receiptUrl;   // 영수증 URL
}
