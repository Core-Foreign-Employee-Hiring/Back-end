package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.PhoneNumberVerification;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.repository.PhoneNumberVerificationRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.exception.UnauthorizedException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final PhoneNumberVerificationRepository phoneNumberVerificationRepository;
    private final MemberRepository memberRepository;

    // 설정값 주입
    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.api.number}")
    private String senderPhoneNumber;

    private DefaultMessageService messageService;

    // CoolSMS SDK 초기화
    public void initializeMessageService() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendVerificationSms(String phoneNumber, LocalDateTime requestedAt) {

        // 핸드폰번호 중복 등록 검증
        if (memberRepository.findByEmail(phoneNumber).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_PHONENUMBER_EXCPETION.getMessage());
        }

        initializeMessageService(); // 메시지 서비스 초기화

        // 기존 인증코드 삭제
        phoneNumberVerificationRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(phoneNumberVerificationRepository::delete);

        // 새 인증코드 생성
        String code = generateSixDigitCode();
        PhoneNumberVerification verification = PhoneNumberVerification.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .expirationTimeInMinutes(5)
                .isVerified(false)
                .build();
        phoneNumberVerificationRepository.save(verification);

        // SMS 발송
        Message message = new Message();
        message.setFrom(senderPhoneNumber);
        message.setTo(phoneNumber);
        message.setText(String.format("[ForWork 인증코드] %s\n인증코드는 5분 후 만료됩니다.", code));

        try {
            SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
        } catch (Exception e) {
            throw new InternalServerException(ErrorStatus.SMS_SEND_FAILED_EXCEPTION.getMessage());
        }

    }

    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1000000); // 0 ~ 999999
        return String.format("%06d", number); // 6자리 코드
    }

    public void verifyCode(String code, LocalDateTime requestedAt) {
        PhoneNumberVerification verification = phoneNumberVerificationRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.WRONG_SMS_VERIFICATION_CODE_EXCEPTION.getMessage()));

        if (verification.isExpired(requestedAt)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_SMS_VERIFICATION_CODE_EXCEPTION.getMessage());
        }

        verification.setIsVerified(true);
        phoneNumberVerificationRepository.save(verification);
    }
}
