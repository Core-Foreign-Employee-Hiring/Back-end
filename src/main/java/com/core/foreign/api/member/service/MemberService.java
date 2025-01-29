package com.core.foreign.api.member.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.BusinessFieldTarget;
import com.core.foreign.api.business_field.entity.BusinessFieldEntity;
import com.core.foreign.api.business_field.repository.BusinessFieldEntityRepository;
import com.core.foreign.api.business_field.service.BusinessFieldUpdater;
import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.repository.CompanyValidationRepository;
import com.core.foreign.api.member.repository.EmailVerificationRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.repository.PhoneNumberVerificationRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PhoneNumberVerificationRepository phoneNumberVerificationRepository;
    private final BusinessFieldUpdater businessFiledUpdater;
    private final BusinessFieldEntityRepository businessFieldEntityRepository;
    private final EmailService emailService;
    private final CompanyValidationRepository companyValidationRepository;

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
        // 핸드폰번호 중복 검증
        if (memberRepository.findByEmail(employeeRegisterRequestDTO.getPhoneNumber()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_PHONENUMBER_EXCPETION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(employeeRegisterRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage());
        }

        // 핸드폰번호 인증 여부 체크
        PhoneNumberVerification phoneNumberVerification = phoneNumberVerificationRepository.findByPhoneNumber(employeeRegisterRequestDTO.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage()));
        if (!phoneNumberVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage());
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
                employeeRegisterRequestDTO.getBirthDate(),
                employeeRegisterRequestDTO.isMale(),
                employeeRegisterRequestDTO.isTermsOfServiceAgreement(),
                employeeRegisterRequestDTO.isOver15(),
                employeeRegisterRequestDTO.isPersonalInfoAgreement(),
                employeeRegisterRequestDTO.isAdInfoAgreementSnsMms(),
                employeeRegisterRequestDTO.isAdInfoAgreementEmail()
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
        // 핸드폰번호 중복 검증
        if (memberRepository.findByEmail(employerRegisterRequestDTO.getPhoneNumber()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_PHONENUMBER_EXCPETION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(employerRegisterRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage());
        }

        // 핸드폰번호 인증 여부 체크
        PhoneNumberVerification phoneNumberVerification = phoneNumberVerificationRepository.findByPhoneNumber(employerRegisterRequestDTO.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage()));
        if (!phoneNumberVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage());
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
                employerRegisterRequestDTO.getBirthDate(),
                employerRegisterRequestDTO.isMale(),
                employerRegisterRequestDTO.isTermsOfServiceAgreement(),
                employerRegisterRequestDTO.isOver15(),
                employerRegisterRequestDTO.isPersonalInfoAgreement(),
                employerRegisterRequestDTO.isAdInfoAgreementSnsMms(),
                employerRegisterRequestDTO.isAdInfoAgreementEmail()
        );

        Employer savedEmployer = memberRepository.save(employer);

        // 업집종 추가.
        businessFiledUpdater.updateBusinessFiledOfEmployer(savedEmployer.getId(), List.of(employerRegisterRequestDTO.getBusinessField()));

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
        Map<String, String> tokens = jwtService.createAccessAndRefreshToken(member.getId());

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
                                        boolean isOver15,
                                        boolean personalInfoAgreement,
                                        boolean adInfoAgreementSmsMms,
                                        boolean adInfoAgreementEmail){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();
        member.updateAgreement(termsOfServiceAgreement, isOver15, personalInfoAgreement, adInfoAgreementSmsMms, adInfoAgreementEmail);

    }



    @Transactional
    public void updateEmployerEmail(Long memberId, String email){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();

        // 이메일 중복 검증
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_EMAIL_EXCPETION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage());
        }

        member.updateEmail(email);

    }

    @Transactional
    public void updateEmployerCompanyEmail(Long memberId, String email){
        // 이미 필터에서 있는 거 확인했음.
        Employer employer = (Employer)memberRepository.findById(memberId).get();

        // 이메일 중복 검증
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_EMAIL_EXCPETION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage());
        }

        employer.updateCompanyEmail(email);

    }

    @Transactional
    public void updateEmployerPhoneNumber(Long memberId, String phoneNumber){
        // 이미 필터에서 있는 거 확인했음.
        Member member = memberRepository.findById(memberId).get();

        // 핸드폰번호 중복 검증
        if (memberRepository.findByEmail(phoneNumber).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_PHONENUMBER_EXCPETION.getMessage());
        }

        // 핸드폰번호 인증 여부 체크
        PhoneNumberVerification phoneNumberVerification = phoneNumberVerificationRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage()));
        if (!phoneNumberVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage());
        }


        member.updatePhoneNumber(phoneNumber);

    }

    @Transactional
    public void updateEmployerCompanyPhoneNumber(Long memberId, String phoneNumber){
        // 이미 필터에서 있는 거 확인했음.
        Employer employer = (Employer)memberRepository.findById(memberId).get();

        // 핸드폰번호 중복 검증
        if (memberRepository.findByEmail(phoneNumber).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_PHONENUMBER_EXCPETION.getMessage());
        }

        // 핸드폰번호 인증 여부 체크
        PhoneNumberVerification phoneNumberVerification = phoneNumberVerificationRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage()));
        if (!phoneNumberVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_PHONENUMBER_VERIFICATION_EXCEPTION.getMessage());
        }


        employer.updateCompanyMainPhoneNumber(phoneNumber);

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




    @Transactional
    public void updateBusinessFiledOfEmployer(Long employerId, List<BusinessField> newFields){
        businessFiledUpdater.updateBusinessFiledOfEmployer(employerId, newFields);
    }


    public EmployerCompanyInfoResponseDTO getCompanyInfo(Long employerId){
        // 인증에서 이미 존재하는 거 검증.
        Employer employer = (Employer)memberRepository.findById(employerId).get();


        List<BusinessField> businessFields = businessFieldEntityRepository.findByTargetAndTargetId(BusinessFieldTarget.EMPLOYER, employerId)
                .stream().map(BusinessFieldEntity::getBusinessField).toList();

        return EmployerCompanyInfoResponseDTO.from(employer, businessFields);

    }

    public EmployeeBasicResumeResponseDTO getEmployeeBasicResume(Long employeeId){
        // 토큰 검사할 때 존재하는 거 알았어
        Employee employee = (Employee) memberRepository.findById(employeeId).get();
        return EmployeeBasicResumeResponseDTO.from(employee);
    }

    @Transactional
    public void updateEmployeeBasicResume(Long employeeId, EmployeeBasicResumeUpdateDTO updateDTO){
        Employee employee = (Employee) memberRepository.findById(employeeId).get();

        employee.updateBasicResume(updateDTO);
    }

    // 사용자 ID 찾기
    public String findUserId(String name, String phoneNumber) {
        Member member = memberRepository.findByNameAndPhoneNumber(name, phoneNumber)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage()));

        return member.getUserId();
    }


    @Transactional
    public void updateEmployerBusinessInfo(Long employerId,String businessNo, String startDate, String representativeName){
        Employer employer = (Employer)memberRepository.findById(employerId).get();


        Optional<CompanyValidation> cv = companyValidationRepository.findByBusinessNoAndStartDateAndRepresentativeName(businessNo, startDate, representativeName);
        if(cv.isEmpty()){
            throw new BadRequestException("사업자등록 정보 진위 확인하세요.");
        }

        employer.updateBusinessInfo(businessNo, startDate, representativeName);
        companyValidationRepository.delete(cv.get());

    }

}
