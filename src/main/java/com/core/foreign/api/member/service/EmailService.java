package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.EmailVerification;
import com.core.foreign.api.member.repository.EmailVerificationRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.UnauthorizedException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String serviceEmail;

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    public void sendVerificationEmail(String email, LocalDateTime requestedAt) {

        //기존에 있는 Email 삭제
        emailVerificationRepository.findByEmail(email)
                .ifPresent(emailVerification -> emailVerificationRepository.delete(emailVerification));

        String code = generateSixDigitCode();
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expirationTimeInMinutes(5)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(verification);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(String.format("ForWork <%s>", serviceEmail));
        mailMessage.setTo(email);
        mailMessage.setSubject("ForWork 회원가입 인증코드 입니다.");
        mailMessage.setText(verification.generateCodeMessage());
        mailSender.send(mailMessage);
    }

    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1000000); // 0 ~ 999999 범위
        return String.format("%06d", number); // 숫자 6자리
    }


    public void verifyEmail(String code, LocalDateTime requestedAt) {
        EmailVerification verification = emailVerificationRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION.getMessage()));

        if (verification.isExpired(requestedAt)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION.getMessage());
        }

        verification.setIsVerified(true);
        emailVerificationRepository.save(verification);
    }
}