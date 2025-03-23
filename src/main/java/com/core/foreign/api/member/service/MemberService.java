package com.core.foreign.api.member.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.repository.EmployerBusinessFieldRepository;
import com.core.foreign.api.business_field.service.BusinessFieldUpdater;
import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.repository.*;
import com.core.foreign.api.portfolio.dto.response.ApplicationPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.response.BasicPortfolioPreviewResponseDTO;
import com.core.foreign.api.recruit.dto.MyResumeResponseDTO;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.api.recruit.dto.internal.ResumeDTO;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import com.core.foreign.api.recruit.service.RecruitDeleter;
import com.core.foreign.api.recruit.service.ResumeDeleter;
import com.core.foreign.api.recruit.service.ResumeReader;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.exception.UnauthorizedException;
import com.core.foreign.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.core.foreign.common.response.ErrorStatus.PASSWORD_VERIFICATION_REQUIRED_EXCEPTION;
import static com.core.foreign.common.response.ErrorStatus.REQUIRED_TERMS_NOT_AGREED_EXCEPTION;

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
    private final CompanyValidationRepository companyValidationRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final EvaluationCreator evaluationCreator;
    private final EmployerBusinessFieldRepository employerBusinessFieldRepository;
    private final EmployerEmployeeRepository employerEmployeeRepository;
    private final EmployerResumeRepository employerResumeRepository;
    private final EvaluationReader evaluationReader;
    private final ResumeDeleter resumeDeleter;
    private final RecruitDeleter recruitDeleter;
    private final EmployeeRepository employeeRepository;
    private final EmployeePortfolioRepository employeePortfolioRepository;
    private final ResumeReader resumeReader;
    private final OrderedFormContentFilter formContentFilter;

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
        if (memberRepository.findByPhoneNumber(employeeRegisterRequestDTO.getPhoneNumber()).isPresent()) {
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

        evaluationCreator.initializeEmployeeEvaluation(employee);
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
        if (memberRepository.findByPhoneNumber(employerRegisterRequestDTO.getPhoneNumber()).isPresent()) {
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

        // 사업자등록번호인증 확인

        String startDate = employerRegisterRequestDTO.getEstablishedDate().toString().replace("-", ""); // "yyyy-MM-dd" -> "yyyyMMdd"

        Optional<CompanyValidation> cv = companyValidationRepository.findByBusinessNoAndStartDateAndRepresentativeName(employerRegisterRequestDTO.getBusinessRegistrationNumber(), startDate, employerRegisterRequestDTO.getRepresentativeName());
        if(cv.isEmpty()){
            log.error("사업자 인증 안 됨.");
            log.info("사업자 등록 번호= {}", employerRegisterRequestDTO.getBusinessRegistrationNumber());
            log.info("startDate= {}", startDate);
            log.info("대표자명= {}", employerRegisterRequestDTO.getRepresentativeName());
            throw new BadRequestException(ErrorStatus.MISSING_BUSINESS_REGISTRATION_VERIFICATION_EXCEPTION.getMessage());
        }

        companyValidationRepository.delete(cv.get());
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

        evaluationCreator.initializeEmployerEvaluation(savedEmployer);
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
                member.getName(),
                member.getUserId(),
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

        if(!isOver15 || !termsOfServiceAgreement || !personalInfoAgreement){
            log.warn("[MemberService][updateEmployerAgreement][필수 약관 동의 안 함.][isOver15= {}, termsOfServiceAgreement= {}, personalInfoAgreement= {}]", isOver15, termsOfServiceAgreement, personalInfoAgreement);
            throw new BadRequestException(REQUIRED_TERMS_NOT_AGREED_EXCEPTION.getMessage());
        }

        member.updateAgreement(termsOfServiceAgreement, isOver15, personalInfoAgreement, adInfoAgreementSmsMms, adInfoAgreementEmail);
    }

    @Transactional
    public void updateMemberEmail(Long memberId, String email){
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

        emailVerificationRepository.delete(emailVerification);
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
        emailVerificationRepository.delete(emailVerification);
    }

    @Transactional
    public void updateMemberPhoneNumber(Long memberId, String phoneNumber){
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

        phoneNumberVerificationRepository.delete(phoneNumberVerification);
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
        phoneNumberVerificationRepository.delete(phoneNumberVerification);
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
    public void updateCompanyAddress(Long memberId, String zipcode, String address1, String address2) {
        // 이미 필터에서 있는 거 확인했음.
        Employer employer = (Employer)memberRepository.findById(memberId).get();
        Address address = new Address(
                zipcode,
                address1,
                address2
        );
        employer.updateCompanyAddress(address);
    }

    @Transactional
    public void updateBusinessFiledOfEmployer(Long employerId, List<BusinessField> newFields){
        businessFiledUpdater.updateBusinessFiledOfEmployer(employerId, newFields);
    }

    public EmployerCompanyInfoResponseDTO getCompanyInfo(Long employerId){
        // 인증에서 이미 존재하는 거 검증.
        Employer employer = (Employer)memberRepository.findById(employerId).get();

        List<BusinessField> businessFields = employerBusinessFieldRepository.findByEmployerId(employerId)
                .stream().map(employerBusinessField -> employerBusinessField.getBusinessFieldEntity().getBusinessField()).toList();

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


    public boolean checkPassword(Long memberId, String password){
        Member member = memberRepository.findById(memberId).get();

        // 비밀번호 검증
        boolean matches = passwordEncoder.matches(password, member.getPassword());

        if(matches){member.updatePasswordVerifiedAt();}

        return matches;
    }

    @Transactional
    public void updateUserId(Long memberId, String userId){
        Member member = memberRepository.findById(memberId).get();

        // 비밀번호 확인했는지 판단.
        if (member.getPasswordVerifiedAt()== null || member.getPasswordVerifiedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new BadRequestException(PASSWORD_VERIFICATION_REQUIRED_EXCEPTION.getMessage());
        }

        try{

            memberRepository.updateUserId(member.getId(), userId);
            member.resetPasswordVerificationTime();

        }catch(DataIntegrityViolationException e){
            e.printStackTrace();
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_USERID_EXCPETION.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Transactional
    public void updateMemberPassword(Long memberId, String password){
        Member member = memberRepository.findById(memberId).get();

        // 비밀번호 확인했는지 판단.
        if (member.getPasswordVerifiedAt()== null || member.getPasswordVerifiedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new BadRequestException(PASSWORD_VERIFICATION_REQUIRED_EXCEPTION.getMessage());
        }

        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage()));
        if (!emailVerification.isVerified()) {
            throw new BadRequestException(ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage());
        }

        emailVerificationRepository.delete(emailVerification);
        String encode = passwordEncoder.encode(password);
        member.updatePassword(encode);
        member.resetPasswordVerificationTime();
    }

    // 비밀번호 초기화
    @Transactional
    public void resetPassword(PasswordResetRequestDTO.PasswordResetConfirm passwordResetConfirm) {

        PasswordReset passwordReset = passwordResetRepository.findByCode(passwordResetConfirm.getCode())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.INVALID_PASSWORD_RESET_CODE_EXCEPTION.getMessage()));

        if (LocalDateTime.now().isAfter(passwordReset.getExpirationTime())) {
            throw new UnauthorizedException(ErrorStatus.EXPIRED_PASSWORD_RESET_CODE_EXCEPTION.getMessage());
        }

        Member member = memberRepository.findByEmail(passwordReset.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage()));

        member.updatePassword(passwordEncoder.encode(passwordResetConfirm.getNewPassword()));
        memberRepository.save(member);

        passwordResetRepository.delete(passwordReset);
    }

    public PageResponseDTO<BasicPortfolioPreviewResponseDTO> getMyBasicPortfolios(Long employerId, Integer page, Integer size){
        Pageable pageable =PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Employee> find = employerEmployeeRepository.findByEmployerId(employerId, pageable).map(EmployerEmployee::getEmployee);

        Map<Long, EmployeeEvaluationCountDTO> employeeEvaluations = evaluationReader.getEmployeeEvaluations(find.getContent());

        Page<BasicPortfolioPreviewResponseDTO> dto = find
                .map((employee -> {
                    EmployeeEvaluationCountDTO employeeEvaluationCountDTO = employeeEvaluations.get(employee.getId());

                    return new BasicPortfolioPreviewResponseDTO(employee, employeeEvaluationCountDTO);
                }));

        PageResponseDTO<BasicPortfolioPreviewResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> getMyApplicationPortfolios(Long employerId, Integer page, Integer size){

        Pageable pageable =PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Resume> applicationPortfolio = employerResumeRepository.findByEmployerId(employerId, pageable).map(EmployerResume::getResume);


        // 평가 갖고 온다.
        List<Employee> employees = applicationPortfolio.map(Resume::getEmployee).toList();
        Map<Long, EmployeeEvaluationCountDTO> employeeEvaluations = evaluationReader.getEmployeeEvaluations(employees);


        // 업직종 갖고 온다.
        List<Recruit> recruits = applicationPortfolio.map(Resume::getRecruit).toList();
        Map<Recruit, List<BusinessField>> businessMap=new HashMap<>();

        for (Recruit recruit : recruits) {
            businessMap.put(recruit, new ArrayList<>());
        }

        /**
         * 갖고 오는 로직.
         */

        Page<ApplicationPortfolioPreviewResponseDTO> dto = applicationPortfolio.map((resume) -> {
            Employee employee = resume.getEmployee();
            Recruit recruit = resume.getRecruit();

            EmployeeEvaluationCountDTO employeeEvaluationCountDTO = employeeEvaluations.get(employee.getId());
            List<BusinessField> businessFields = businessMap.get(recruit);

            return new ApplicationPortfolioPreviewResponseDTO(resume, employeeEvaluationCountDTO, businessFields);

        });

        PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }


    @Transactional
    public void withdrawMember(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("[withdrawMember][member not found][memberId= {}]", memberId);
                    return new NotFoundException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage());
                });

        if(member instanceof Employer employer){
            recruitDeleter.deleteRecruitOnEmployeeWithdrawal(employer.getId());

            employer.withdraw();
        }else if (member instanceof Employee employee){
            resumeDeleter.deleteResumeOnEmployeeWithdrawal(employee.getId());

            employee.withdraw();
        }
    }

    public MyResumeResponseDTO getMyResume(Long employeeId, Long resumeId){
        // 내 회원 정보
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("[getMyResume][employee not found][employeeId= {}]", employeeId);
                    return new NotFoundException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage());
                });

        // 내 스펙 및 경력
        EmployeePortfolioDTO dto = employeePortfolioRepository.findEmployeePortfolioByEmployeeId(employeeId)
                .map(EmployeePortfolioDTO::from)
                .orElseGet(EmployeePortfolioDTO::emptyPortfolio);

        // 이력서
        ResumeDTO myResume = resumeReader.getMyResume(employeeId, resumeId);

        MyResumeResponseDTO response = MyResumeResponseDTO.of(employee, dto, myResume);

        return response;
    }
}
