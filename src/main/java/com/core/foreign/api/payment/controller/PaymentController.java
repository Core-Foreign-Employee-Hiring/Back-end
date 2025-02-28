package com.core.foreign.api.payment.controller;

import com.core.foreign.api.payment.dto.*;
import com.core.foreign.api.payment.service.PaymentService;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.ErrorStatus;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpResponse;

@Tag(name = "Payment", description = "결제 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "초기 결제 세팅 API",
            description = "프론트엔드에서 결제 요청 전 수행해야하는 결제 세팅 API 입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "결제 정보 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/ready")
    public ResponseEntity<ApiResponse<Void>> readyPayment(@RequestBody PaymentRequestDTO paymentRequestDTO,
                                                      @AuthenticationPrincipal SecurityMember securityMember) {
        paymentService.readyPayment(paymentRequestDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.SEND_PAY_INFO_SAVE_SUCCESS);
    }

    @Operation(
            summary = "결제 승인 요청 API",
            description = "토스페이먼트에 결제 승인 요청을 수행합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "결제 승인 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "결제 정보 저장 실패")
    })
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> confirmPayment(@RequestBody ApprovalRequestDTO approvalRequestDTO,
                                                                          @AuthenticationPrincipal SecurityMember securityMember) {
        try {
            // 토스에 결제 승인 요청 시도
            HttpResponse<String> response = paymentService.requestConfirm(approvalRequestDTO,securityMember.getId());

            if (response.statusCode() == 200) {
                try {
                    // 결제 된 정보 저장
                    PaymentResponseDTO paymentResponse = paymentService.saveConfirmedPayment(approvalRequestDTO, response);
                    return ApiResponse.success(SuccessStatus.SEND_PAY_SUCCESS, paymentResponse);
                   } catch (Exception e) {
                    // 백엔드 내부적 오류로 결제 정보 저장 실패했을경우 토스에 결제 취소 요청
                    paymentService.requestPaymentCancel(approvalRequestDTO.getPaymentKey(), ErrorStatus.FAIL_PAY_EXCEPTION.getMessage(),securityMember.getId(), 0);
                    throw new InternalServerException(ErrorStatus.FAIL_PAY_EXCEPTION.getMessage());
                }
            } else {
                throw new InternalServerException(ErrorStatus.FAIL_PAY_EXCEPTION.getMessage());
            }
        } catch (IOException | InterruptedException e) {
            throw new InternalServerException(ErrorStatus.FAIL_PAYING_EXCEPTION.getMessage());
        }
    }

    @Operation(
            summary = "결제 취소 API",
            description = "토스페이먼트에 결제 취소 요청을 수행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(@RequestBody CancelRequestDTO cancelRequestDTO,
                                                           @AuthenticationPrincipal SecurityMember securityMember) {
        try{
            paymentService.requestPaymentCancel(cancelRequestDTO.getPaymentKey(), cancelRequestDTO.getCancelReason(), securityMember.getId(),1);
        }catch (IOException | InterruptedException e){
            throw new InternalServerException(ErrorStatus.FAIL_PAY_CANCEL_EXCEPTION.getMessage());
        }
        return ApiResponse.success_only(SuccessStatus.SEND_CANCELED_PAY_SUCCESS);
    }

    @Operation(
            summary = "결제 내역 조회. API",
            description = "결제 내역을 확인할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PageResponseDTO<PaymentHistoryResponseDTO>>> getPaymentHistory(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                     @RequestParam("page") Integer page,
                                                                                                     @RequestParam("size") Integer size) {
        PageResponseDTO<PaymentHistoryResponseDTO> paymentHistory = paymentService.getPaymentHistory(securityMember.getId(), page, size);

        return ApiResponse.success(SuccessStatus.SEND_PAYMENT_HISTORY_SUCCESS, paymentHistory);
    }
}
