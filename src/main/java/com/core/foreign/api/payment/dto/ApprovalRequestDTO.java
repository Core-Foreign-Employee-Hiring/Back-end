package com.core.foreign.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDTO {
    private String paymentKey;    // 결제 키
    private String orderId;       // 주문 ID
    private Long amount;          // 결제 금액
}
