package com.core.foreign.api.member.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.service.BusinessFiledUpdater;
import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.repository.EmailVerificationRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final DuplicationValidator duplicationValidator;
    private final BusinessFiledUpdater businessFiledUpdater;

    // 고용인 회원가입
    @Transactional
    public void registerEmployee(EmployeeRegisterRequestDTO employeeRegisterRequestDTO) {
        // 사용자ID 중복 검증
        if (memberRepository.findByUserId(employeeRegisterRequestDTO.getUserId()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_USERID_EXCPETION.getMessage());
        }
        // 이메일 중복 검증
        if (memberRepository.findByEmail(employeeRegisterRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_EMAIL_EXCPETION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(employeeRegisterRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCPETION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCPETION.getMessage());
        }

        Address address = new Address(
                employeeRegisterRequestDTO.getZipcode(),
                employeeRegisterRequestDTO.getAddress1(),
                employeeRegisterRequestDTO.getAddress2()
        );

        // Employee 엔티티 생성
        Employee employee = new Employee(
                employeeRegisterRequestDTO.getUserId(),
                passwordEncoder.encode(employeeRegisterRequestDTO.getPassword()), // 비밀번호 암호화
                employeeRegisterRequestDTO.getName(),
                employeeRegisterRequestDTO.getEmail(),
                employeeRegisterRequestDTO.getPhoneNumber(),
                address,
                employeeRegisterRequestDTO.getNationality(),
                employeeRegisterRequestDTO.getEducation(),
                employeeRegisterRequestDTO.getVisa(),
                LocalDate.now(),   // 임시
                true  ,             // 임시
                true,               // 임시
                true,               // 임시
                true,               // 임시
                true                // 임시
        );

        memberRepository.save(employee);
    }

    // 고용주 회원가입
    @Transactional
    public void registerEmployer(EmployerRegisterRequestDTO employerRegisterRequestDTO) {
        // 사용자ID 중복 검증
        if (memberRepository.findByUserId(employerRegisterRequestDTO.getUserId()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_USERID_EXCPETION.getMessage());
        }
        // 이메일 중복 검증
        if (memberRepository.findByEmail(employerRegisterRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_EMAIL_EXCPETION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(employerRegisterRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCPETION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCPETION.getMessage());
        }

        Address address = new Address(
                employerRegisterRequestDTO.getZipcode(),
                employerRegisterRequestDTO.getAddress1(),
                employerRegisterRequestDTO.getAddress2()
        );

        // Employer 엔티티 생성
        Employer employer = new Employer(
                employerRegisterRequestDTO.getUserId(),
                passwordEncoder.encode(employerRegisterRequestDTO.getPassword()), // 비밀번호 암호화
                employerRegisterRequestDTO.getName(),
                employerRegisterRequestDTO.getEmail(),
                employerRegisterRequestDTO.getPhoneNumber(),
                address,
                employerRegisterRequestDTO.getBusinessRegistrationNumber(),
                employerRegisterRequestDTO.getCompanyName(),
                employerRegisterRequestDTO.getEstablishedDate(),
                employerRegisterRequestDTO.getBusinessField(),
                LocalDate.now(),   // 임시
                true,              // 임시
                true,               // 임시
                true,               // 임시
                true,               // 임시
                true                // 임시
        );

        memberRepository.save(employer);
    }

    // 로그인
    public MemberLoginResponseDTO login(MemberLoginRequestDTO dto) {
        // userId로 회원 검색
        Member member = memberRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.WRONG_PASSWORD_EXCEPTION.getMessage());
        }

        // JWT 토큰 생성 (Access, Refresh)
        Map<String, String> tokens = jwtService.createAccessAndRefreshToken(member.getEmail());

        // DTO를 사용하여 응답 데이터 구성
        return new MemberLoginResponseDTO(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                member.getRole().name()
        );
    }

    // 사용자 ID 중복 체크
    public void verificationUserId(String userId) {
        // 사용자 ID 중복 검증
        if (memberRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_USERID_EXCPETION.getMessage());
        }
    }

    public EmployerProfileResponseDTO getEmployerProfile(Long  memberId) {
        // 이미 필터에서 있는 거 확인함.
        Member findMember = memberRepository.findById(memberId).get();

        Employer employer = (Employer) findMember;

        return EmployerProfileResponseDTO.from(employer);
    }


    /**
     *
     * @implNote
     * 고용주 이름, 생년월일, 성별 변경.
     *
     * Employer 가 아니라 Member 인데? Employer 보장?
     */
    @Transactional
    public void updateEmployerBasicInfo(Long memberId, String name, LocalDate birthday, boolean isMale){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();
        member.updateBasicInfo(name, birthday, isMale);
    }


    /**
     *
     *
     * @implNote
     * 고용주  약관 수정
     */
    @Transactional
    public void updateEmployerAgreement(Long memberId,
                                        boolean termsOfServiceAgreement,
                                        boolean personalInfoAgreement,
                                        boolean adInfoAgreementSmsMms,
                                        boolean adInfoAgreementEmail){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();
        member.updateAgreement(termsOfServiceAgreement, personalInfoAgreement, adInfoAgreementSmsMms, adInfoAgreementEmail);

    }

    @Transactional
    public void updateEmployerEmail(Long memberId, String email){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();

        member.updateEmail(email);

        // email 중복 테이블에서 삭제.
        duplicationValidator.removeEmailDuplication(email);
    }

    @Transactional
    public void updateEmployerPhoneNumber(Long memberId, String phoneNumber){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();
        member.updatePhoneNumber(phoneNumber);

        // phoneNumber 중복 테이블에서 삭제.
        duplicationValidator.removePhoneNumberDuplication(phoneNumber);
    }


    @Transactional
    public void updateEmployerAddress(Long memberId, String zipcode, String address1, String address2) {
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();
        Address address = new Address(
                zipcode,
                address1,
                address2
        );

        member.updateAddress(address);
    }


    /**
     *

     *
     * 이메일 중복을 확인합니다.
     * 1) Member Table 에서 이미 사용 중인지
     * 2) 누군가 이미 변경하기 위해 찜해 뒀음.
     *
     * 변경할 email 을 미리 찜해두는 것임.
     *  ** 만약, 찜해두고 변경 하지 않을 경우 찜 해제해야 함. <- 이 부분 프론트랑 상의할 것 **
     *  찜 해제하기 위해 프론트가 백엔드에게 알려줬음 좋겠음.
     */
    @Transactional
    public boolean isDuplicateEmail(String email) {
        // 1)
        boolean b = memberRepository.existsByEmail(email);
        if(b){
            throw new BadRequestException("중복된 이메일입니다.");
        }


        duplicationValidator.validateEmailDuplication(email);


        return true;
    }

    @Transactional
    public boolean isDuplicatePhoneNumber(String phoneNumber) {
        // 1)
        boolean b = memberRepository.existsByPhoneNumber(phoneNumber);
        if(b){
            throw new BadRequestException("중복된 전화 번호입니다.");
        }


        duplicationValidator.validatePhoneNumberDuplication(phoneNumber);


        return true;
    }

    @Transactional
    public void updateBusinessFiledOfEmployer(Long employerId, List<BusinessField> newFields){
        businessFiledUpdater.updateBusinessFiledOfEmployer(employerId, newFields);
    }
}
