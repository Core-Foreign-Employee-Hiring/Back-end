package com.core.foreign.api.payment.service;

import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.payment.dto.ApprovalRequestDTO;
import com.core.foreign.api.payment.dto.PaymentHistoryResponseDTO;
import com.core.foreign.api.payment.dto.PaymentRequestDTO;
import com.core.foreign.api.payment.dto.PaymentResponseDTO;
import com.core.foreign.api.payment.entity.Payment;
import com.core.foreign.api.payment.repository.PaymentRepository;
import com.core.foreign.api.recruit.entity.PremiumManage;
import com.core.foreign.api.recruit.repository.PremiumManageRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PremiumManageRepository premiumManageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    @Value("${toss.payments.base-url}")
    private String baseUrl;

    // 결제 준비
    @Transactional
    public void readyPayment(PaymentRequestDTO paymentRequestDTO, Long memberId) {

        // 회원 정보 가져오기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        // 기존 결제 정보 중복 체크
        if (paymentRepository.findByOrderId(paymentRequestDTO.getOrderId()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_READY_PAYMENT_EXCEPTION.getMessage());
        }

        // 초기 결제 정보 생성
        Payment payment = Payment.builder()
                .orderId(paymentRequestDTO.getOrderId())
                .totalAmount(paymentRequestDTO.getAmount())
                .requestedAt(LocalDateTime.now())
                .status("READY")
                .member(member)
                .build();

        paymentRepository.save(payment);
    }

    // 결제 승인
    public HttpResponse<String> requestConfirm(ApprovalRequestDTO approvalRequestDTO, Long memberId) throws IOException, InterruptedException {
        String orderId = approvalRequestDTO.getOrderId();
        Long amount = approvalRequestDTO.getAmount();
        String paymentKey = approvalRequestDTO.getPaymentKey();

        // 기존 결제 정보 확인 및 검증
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PAYMENT_INFO_NOT_FOUND_EXCEPTION.getMessage()));

        // 이미 결제 승인 되어있는 경우 예외처리
        if ("DONE".equals(payment.getStatus())) {
            throw new BadRequestException(ErrorStatus.ALREADY_DONE_PAYMENT_EXCEPTION.getMessage());
        }

        if (!payment.getTotalAmount().equals(amount)) {
            throw new BadRequestException(ErrorStatus.WRONG_PAY_AMOUNT_EXCEPTION.getMessage());
        }

        // 결제 승인 요청 JSON 생성
        JsonNode requestObj = objectMapper.createObjectNode()
                .put("orderId", orderId)
                .put("amount", amount)
                .put("paymentKey", paymentKey);

        String requestBody = objectMapper.writeValueAsString(requestObj);

        // 결제 승인 API 호출
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/payments/confirm"))
                .header("Authorization", getAuthorizations())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    // 최종 결제 정보 저장
    @Transactional
    public PaymentResponseDTO saveConfirmedPayment(ApprovalRequestDTO approvalRequestDTO, HttpResponse<String> response) throws IOException {
        // 결제 정보 조회
        Payment payment = paymentRepository.findByOrderId(approvalRequestDTO.getOrderId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PAYMENT_INFO_NOT_FOUND_EXCEPTION.getMessage()));

        // 응답 JSON 파싱
        JsonNode responseJson = objectMapper.readTree(response.body());

        // 날짜 파싱을 위한 포맷 설정
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // 응답 데이터에서 필요한 필드 추출
        String paymentKey = responseJson.get("paymentKey").asText();
        String orderName = responseJson.get("orderName").asText();
        String status = responseJson.get("status").asText();
        String method = responseJson.get("method").asText();
        String receiptUrl = responseJson.get("receipt").get("url").asText();
        Long totalAmount = responseJson.get("totalAmount").asLong();
        Long balanceAmount = responseJson.get("balanceAmount").asLong();
        Long suppliedAmount = responseJson.get("suppliedAmount").asLong();
        Long vat = responseJson.get("vat").asLong();
        String currency = responseJson.get("currency").asText();
        String lastTransactionKey = responseJson.get("lastTransactionKey").asText();

        LocalDateTime approvedAt = responseJson.has("approvedAt") && !responseJson.get("approvedAt").isNull()
                ? LocalDateTime.parse(responseJson.get("approvedAt").asText(), formatter)
                : null;

        // 카드 결제 정보가 있는 경우 카드 정보 추출
        JsonNode cardInfo = responseJson.get("card");
        String cardNumber = null, cardType = null, cardOwnerType = null, issuerCode = null, acquirerCode = null, approveNo = null, acquireStatus = null;
        Integer installmentPlanMonths = null;
        Boolean isInterestFree = null, useCardPoint = null;

        if (cardInfo != null) {
            cardNumber = cardInfo.has("number") ? cardInfo.get("number").asText() : null;
            cardType = cardInfo.has("cardType") ? cardInfo.get("cardType").asText() : null;
            cardOwnerType = cardInfo.has("ownerType") ? cardInfo.get("ownerType").asText() : null;
            issuerCode = cardInfo.has("issuerCode") ? cardInfo.get("issuerCode").asText() : null;
            acquirerCode = cardInfo.has("acquirerCode") ? cardInfo.get("acquirerCode").asText() : null;
            approveNo = cardInfo.has("approveNo") ? cardInfo.get("approveNo").asText() : null;
            acquireStatus = cardInfo.has("acquireStatus") ? cardInfo.get("acquireStatus").asText() : null;
            installmentPlanMonths = cardInfo.has("installmentPlanMonths") ? cardInfo.get("installmentPlanMonths").asInt() : null;
            isInterestFree = cardInfo.has("isInterestFree") ? cardInfo.get("isInterestFree").asBoolean() : null;
            useCardPoint = cardInfo.has("useCardPoint") ? cardInfo.get("useCardPoint").asBoolean() : null;
        }

        Payment updatedPayment = payment.toBuilder()
                .paymentKey(paymentKey)
                .orderName(orderName)
                .status(status)
                .method(method)
                .receiptUrl(receiptUrl)
                .totalAmount(totalAmount)
                .balanceAmount(balanceAmount)
                .suppliedAmount(suppliedAmount)
                .vat(vat)
                .currency(currency)
                .lastTransactionKey(lastTransactionKey)
                .approvedAt(approvedAt)
                .cardNumber(cardNumber)
                .cardType(cardType)
                .cardOwnerType(cardOwnerType)
                .issuerCode(issuerCode)
                .acquirerCode(acquirerCode)
                .approveNo(approveNo)
                .acquireStatus(acquireStatus)
                .installmentPlanMonths(installmentPlanMonths)
                .isInterestFree(isInterestFree)
                .useCardPoint(useCardPoint)
                .build();

        paymentRepository.save(updatedPayment);

        Member member = memberRepository.findById(payment.getMember().getId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage()));

        // "프리미엄 공고" 제품 구매 -> 프리미엄 공고 등록 횟수 증가
        if ("프리미엄 공고".equals(orderName.trim())) {

            PremiumManage premiumManage = premiumManageRepository.findByEmployerId(member.getId())
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

            PremiumManage updatedPremiumManage = premiumManage.increasePremiumCount();
            premiumManageRepository.save(updatedPremiumManage);
        }

        return new PaymentResponseDTO(
                updatedPayment.getOrderId(),
                updatedPayment.getOrderName(),
                updatedPayment.getStatus(),
                updatedPayment.getTotalAmount(),
                updatedPayment.getMethod(),
                updatedPayment.getRequestedAt().toString(),
                updatedPayment.getApprovedAt() != null ? updatedPayment.getApprovedAt().toString() : null,
                updatedPayment.getReceiptUrl()
        );
    }

    // 결제 취소
    public void requestPaymentCancel(String paymentKey, String cancelReason, Long memberId, int status) throws IOException, InterruptedException {

        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 결제 정보 가져오기
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PAYMENT_INFO_NOT_FOUND_EXCEPTION.getMessage()));

        // 이미 취소되어있는 경우 예외처리
        if ("CANCELED".equals(payment.getStatus())) {
            throw new BadRequestException(ErrorStatus.ALREADY_CANCELED_PAYMENT_EXCEPTION.getMessage());
        }

        String cancelJson = objectMapper.writeValueAsString(
                objectMapper.createObjectNode().put("cancelReason", cancelReason)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/payments/" + paymentKey + "/cancel"))
                .header("Authorization", getAuthorizations())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cancelJson))
                .build();

        // status = 1 : 관리자만 수행 가능
        if (status == 1) {

            // 여기 관리자 체크 코드 부여 해야함

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 코드가 200이 아닐 경우 예외 발생
            if (response.statusCode() != 200) {
                throw new BadRequestException(ErrorStatus.FAIL_PAY_CANCEL_EXCEPTION.getMessage());
            }

            saveCanceledPayment(paymentKey, response);

        } else if (status == 0) {

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 코드가 200이 아닐 경우 예외 발생
            if (response.statusCode() != 200) {
                throw new BadRequestException(ErrorStatus.FAIL_PAY_CANCEL_EXCEPTION.getMessage());
            }
        }
    }

    @Transactional
    public void saveCanceledPayment(String paymentKey, HttpResponse<String> response) throws IOException {

        // 결제 정보 조회
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PAYMENT_INFO_NOT_FOUND_EXCEPTION.getMessage()));

        // 응답 JSON 파싱
        JsonNode responseJson = objectMapper.readTree(response.body());

        String status = responseJson.get("status").asText();
        String lastTransactionKey = responseJson.get("lastTransactionKey").asText();

        // 취소 정보 가져오기 (취소 리스트에서 첫 번째 취소 데이터 사용)
        JsonNode cancelsArray = responseJson.get("cancels");
        if (cancelsArray == null || !cancelsArray.isArray() || cancelsArray.isEmpty()) {
            throw new BadRequestException(ErrorStatus.CANCEL_INFO_NOT_FOUND_EXCEPTION.getMessage());
        }

        JsonNode cancelInfo = cancelsArray.get(0);
        String cancelReason = cancelInfo.get("cancelReason").asText();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime canceledAt = LocalDateTime.parse(cancelInfo.get("canceledAt").asText(), formatter);

        Payment updatedPayment = payment.toBuilder()
                .status(status)
                .lastTransactionKey(lastTransactionKey)
                .canceledAt(canceledAt)
                .cancelReason(cancelReason)
                .build();

        paymentRepository.save(updatedPayment);
    }

    // 토스 API 요청 헤더 생성
    private String getAuthorizations() {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }


    public Page<PaymentHistoryResponseDTO> getPaymentHistory(Long memberId, Integer page){
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "id"));

        Page<PaymentHistoryResponseDTO> response= paymentRepository.findAllByMemberId(memberId, pageable)
                .map(PaymentHistoryResponseDTO::from);

        return response;
    }
}