package com.core.foreign.api.member.service;

import com.core.foreign.api.member.dto.EmployeeRegisterRequestDTO;
import com.core.foreign.api.member.dto.EmployerRegisterRequestDTO;
import com.core.foreign.api.member.dto.MemberLoginRequestDTO;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.repository.EmailVerificationRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;

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
                employeeRegisterRequestDTO.getVisa()
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
                employerRegisterRequestDTO.getBusinessField()
        );

        memberRepository.save(employer);
    }

    // 로그인
    public Map<String, Object> login(MemberLoginRequestDTO dto) {
        // userId로 회원 검색
        Member member = memberRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.WRONG_PASSWORD_EXCEPTION.getMessage());
        }

        // JWT 토큰 생성 (Access, Refresh)
        Map<String, String> tokens = jwtService.createAccessAndRefreshToken(member.getEmail());

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", tokens.get("accessToken"));
        response.put("refreshToken", tokens.get("refreshToken"));
        response.put("role", member.getRole().name());

        return response;
    }

    // 사용자 ID 중복 체크
    public void verificationUserId(String userId) {
        // 사용자 ID 중복 검증
        if (memberRepository.findByUserId(userId).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_USERID_EXCPETION.getMessage());
        }
    }
}
