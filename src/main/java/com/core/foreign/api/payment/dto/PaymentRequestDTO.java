package com.core.foreign.api.payment.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long amount;        // 결제 금액
    private String orderId;       // 주문 ID
}
