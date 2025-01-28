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
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final DuplicationUtil duplicationUtil;
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

    /**
     * 지우기 아까움 ㅜㅜ
     * @deprecated
     *

     */
    @Transactional
    public String sendVerificationEmail(Long memberId, String email, String token) {
        // 이메일 형식 처리.

        // Apache Commons EmailValidator 검증
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException(ErrorStatus.VALIDATION_EMAIL_FORMAT_EXCEPTION.getMessage());
        }

        // 이메일 중복 처리.
        boolean isToken = false;
        boolean emailDuplicated = duplicationUtil.isEmailDuplicated(email);
        if (emailDuplicated) { // 중복 o
            // 중복 토근이 없으면.
            if (token == null) {
                throw new BadRequestException("중복된 이메일입니다.");
            }

            // 중복 토큰이 있으면
            if (!jwtService.isTokenValid(token)) { // 토큰이 잘못되었을 경우.
                log.info("토큰이 이상한데...");

                /**
                 * 현재 토큰에 문제가 있는 경우임.
                 *
                 * 1) 토큰 새로해서 발급해주기 <- 현재 유저가 어떤 토큰을 발급 받았는디 DB 에서 유지해줘야 함.
                 * 2) 그냥 중복 이메일 처리 <- 실수로 뒤로가기 해결을 위해 토큰 방식을 사용하는데 목적에 전혀 맞지 않음
                 *
                 * 현재 2)이지만 나중에 리펙토링할 때 1) 로 수정해줘야 함.
                 *
                 */
                throw new BadRequestException("중복된 이메일입니다.");
            }
            String duplicationIdString = jwtService.extractDuplicationId(token).get();
            Long duplicationId = Long.valueOf(duplicationIdString);

            Optional<EmailDuplication> duplicationMail = duplicationUtil.getDuplicationMail(duplicationId);

            if (duplicationMail.isEmpty()) {
                /**
                 * token에 있는 메일이 DB 에 없을 경우 <- 스케줄링 잘못돌린 경우 같은데 token 의 수명이 스케줄링 주기보단 짦아야겠따.
                 */

                log.info("token 의 메일이 DB에 없음.");

                throw new BadRequestException("중복된 이메일입니다.");
            }

            EmailDuplication emailDuplication = duplicationMail.get();
            String tokenEmail = emailDuplication.getEmail();

            if(email.equals(tokenEmail)){
                isToken = true;
            }else{  // 다른 이메일로 변경을 신청한 거야. 이럴 경우 다른 이메일이 우선.
                
            }
            
            
        }

        // 중복 x 면
        log.info("중복된 이메일이 아닙니다.");

        // 이메일 코드 발송
        emailService.sendVerificationEmail(email, LocalDateTime.now());

        // 중복 토큰 발급.

        String duplicationToken;
        if (isToken) {
            duplicationToken = token;
        } else {
            Long duplicationMailId = duplicationUtil.getDuplicationMailId(email);
            duplicationToken = jwtService.createDuplicationToken(duplicationMailId);

        }

        return duplicationToken;
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

        // email 중복 테이블에서 삭제.
//        duplicationUtil.removeEmailDuplication(email);
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

        // email 중복 테이블에서 삭제.
//        duplicationUtil.removeEmailDuplication(email);
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


    /**
     * @deprecated
     * *
     * 이메일 중복을 확인합니다.
     * 1) Member Table 에서 이미 사용 중인지
     * 2) 누군가 이미 변경하기 위해 찜해 뒀음.
     *
     * 변경할 email 을 미리 찜해두는 것임.
     *  ** 만약, 찜해두고 변경 하지 않을 경우 찜 해제해야 함. <- 이 부분 프론트랑 상의할 것 **
     *  찜 해제하기 위해 프론트가 백엔드에게 알려줬음 좋겠음.
     *  *
     */
    @Transactional
    public boolean isDuplicateEmail(String email) {
        // 1)
        boolean b = memberRepository.existsByEmail(email);
        if(b){
            throw new BadRequestException("중복된 이메일입니다.");
        }


        duplicationUtil.validateEmailDuplication(email);


        return true;
    }

    /**
     *
     *
     * @deprecated
     */

    @Transactional
    public boolean isDuplicatePhoneNumber(String phoneNumber) {
        // 1)
        boolean b = memberRepository.existsByPhoneNumber(phoneNumber);
        if(b){
            throw new BadRequestException("중복된 전화 번호입니다.");
        }


        duplicationUtil.validatePhoneNumberDuplication(phoneNumber);


        return true;
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
