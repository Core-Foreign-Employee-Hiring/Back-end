package com.core.foreign.api.member.service;

import com.core.foreign.api.member.dto.PasswordResetRequestDTO;
import com.core.foreign.api.member.entity.EmailVerification;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.entity.PasswordReset;
import com.core.foreign.api.member.repository.EmailVerificationRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.repository.PasswordResetRepository;
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
    private final MemberRepository memberRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetRepository passwordResetRepository;

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

    public void sendPasswordResetEmail(PasswordResetRequestDTO.PasswordResetRequest passwordResetRequest) {

        Member member = memberRepository.findByUserIdAndEmailAndName(passwordResetRequest.getUserId(), passwordResetRequest.getEmail(), passwordResetRequest.getName())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage()));

        String resetCode = generateRandomCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        PasswordReset passwordReset = PasswordReset.builder()
                .email(passwordResetRequest.getEmail())
                .code(resetCode)
                .expirationTime(expirationTime)
                .build();

        passwordResetRepository.save(passwordReset);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(String.format("ForWork <%s>", serviceEmail));
        mailMessage.setTo(passwordResetRequest.getEmail());
        mailMessage.setSubject("ForWork 비밀번호 초기화 링크");
        mailMessage.setText("비밀번호를 재설정하려면 아래 링크를 클릭하세요:\n\n"
                + "https://www.forwork.co.kr/password?code=" + resetCode + "\n\n"
                + "이 링크는 5분간 유효합니다.");

        mailSender.send(mailMessage);
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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