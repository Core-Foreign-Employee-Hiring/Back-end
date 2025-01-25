package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.EmailDuplication;
import com.core.foreign.api.member.entity.PhoneNumberDuplication;
import com.core.foreign.api.member.repository.EmailDuplicationRepository;
import com.core.foreign.api.member.repository.PhoneNumberDuplicationRepository;
import com.core.foreign.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DuplicationValidator {
    private final EmailDuplicationRepository emailDuplicationRepository;
    private final PhoneNumberDuplicationRepository phoneNumberDuplicationRepository;


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
