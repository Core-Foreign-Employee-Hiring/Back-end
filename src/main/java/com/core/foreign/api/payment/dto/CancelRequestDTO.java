package com.core.foreign.api.payment.dto;

import lombok.Data;

@Data
public class CancelRequestDTO {
    private String paymentKey;    // 결제 키
    private String cancelReason;  // 취소 사유
}
