package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.EmailDuplication;
import com.core.foreign.api.member.entity.PhoneNumberDuplication;
import com.core.foreign.api.member.repository.EmailDuplicationRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.repository.PhoneNumberDuplicationRepository;
import com.core.foreign.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DuplicationUtil {
    private final EmailDuplicationRepository emailDuplicationRepository;
    private final PhoneNumberDuplicationRepository phoneNumberDuplicationRepository;
    private final MemberRepository memberRepository;

    public Optional<EmailDuplication> getDuplicationMail(Long duplicationId){
        return emailDuplicationRepository.findById(duplicationId);
    }

    public Long getDuplicationMailId(String email){
        EmailDuplication emailDuplication = emailDuplicationRepository.findByEmail(email).get();

        return emailDuplication.getId();
    }

    public boolean isEmailDuplicated(String email) {
        // 이미 이메일이 존재하면 true 반환
        if (memberRepository.existsByEmail(email)) {
            return true;
        }

        try {
            // 이메일 중복 검사 기록 저장
            emailDuplicationRepository.save(new EmailDuplication(email));
        } catch (DataIntegrityViolationException e) {
            // 중복 저장 시도 발생 시 true 반환
            return true;
        }

        // 중복이 아님
        return false;
    }


    void validateEmailDuplication(String email) {
        EmailDuplication emailDuplication = new EmailDuplication(email);

        try {
            emailDuplicationRepository.save(emailDuplication);

        }catch (DataIntegrityViolationException e){
            log.info("중복된 이메일입니다.");

            throw new BadRequestException("중복된 이메일입니다.");
        }
    }


    void validatePhoneNumberDuplication(String phoneNumber ) {
        PhoneNumberDuplication phoneNumberDuplication = new PhoneNumberDuplication(phoneNumber);

        try {
            phoneNumberDuplicationRepository.save(phoneNumberDuplication);

        }catch (DataIntegrityViolationException e){
            log.info("중복된 전화번호입니다.");

            throw new BadRequestException("중복된 전화번호입니다.");
        }
    }

    void removeEmailDuplication(String email) {
        emailDuplicationRepository.deleteByEmail(email);
    }

    void removePhoneNumberDuplication(String phoneNumber) {
        phoneNumberDuplicationRepository.deleteByPhoneNumber(phoneNumber);
    }


}
