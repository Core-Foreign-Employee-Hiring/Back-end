package com.core.foreign.api.member.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.member.dto.EmployeePortfolioAwardDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioCertificationDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioExperienceDTO;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.repository.EmployeePortfolioBusinessFieldInfoRepository;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldType.*;
import static com.core.foreign.api.member.entity.EmployeePortfolioStatus.COMPLETED;
import static com.core.foreign.api.member.entity.EmployeePortfolioStatus.TEMPORARY;
import static com.core.foreign.common.response.ErrorStatus.PORTFOLIO_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeePortfolioService {
    private final S3Service s3Service;
    private final EmployeePortfolioRepository employeePortfolioRepository;
    private final EmployeePortfolioBusinessFieldInfoRepository employeePortfolioBusinessFieldInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createEmployeePortfolio(Long employeeId, EmployeePortfolioDTO dto, EmployeePortfolioStatus employeePortfolioStatus){
        Employee employee = (Employee) memberRepository.findById(employeeId).get();

        // 임시 등록 시 기존 임시 등록은 삭제합니다.
        if(employeePortfolioStatus==TEMPORARY){
            clearTemporarySave(employeeId);
        }

        EmployeePortfolio build = EmployeePortfolio.builder()
                .introduction(dto.getIntroduction())
                .enrollmentCertificateUrl(dto.getEnrollmentCertificateUrl())
                .transcriptUrl(dto.getTranscriptUrl())
                .partTimeWorkPermitUrl(dto.getPartTimeWorkPermitUrl())
                .topic(dto.getTopic())
                .englishTestType(dto.getEnglishTestType())
                .englishTestScore(dto.getEnglishTestScore())
                .employeePortfolioStatus(employeePortfolioStatus)
                .employee(employee)
                .build();

        EmployeePortfolio portfolio = employeePortfolioRepository.save(build);

        saveEmployeePortfolioBusinessFieldInfo(portfolio, dto);

        if(dto.isPortfolioPublic()){employee.publicizePortfolio();}
        else{employee.privatizePortfolio();}

    }


    public EmployeePortfolioDTO getEmployeePortfolio(Long employeeId, EmployeePortfolioStatus status) {
        EmployeePortfolio portfolio = employeePortfolioRepository.findByEmployeeId(employeeId, status)
                .orElseThrow(() -> {
                    log.error("포트폴리오 없음. employeeId={}, status={}", employeeId, status);
                    return new BadRequestException(PORTFOLIO_NOT_FOUND_EXCEPTION.getMessage());
                });

        Employee employee = portfolio.getEmployee();
        EmployeePortfolioDTO dto = EmployeePortfolioDTO.from(portfolio, employee.isPortfolioPublic());

        return dto;
    }

    @Transactional
    public void updateEmployeePortfolio(Long employeeId, EmployeePortfolioDTO dto) {
        Optional<EmployeePortfolio>  find= employeePortfolioRepository.findByEmployeeId(employeeId, COMPLETED);
        if(find.isEmpty()) {
            log.error("[EmployeePortfolioService][updateEmployeePortfolio][왜 없니...]");

            return;
        }

        EmployeePortfolio employeePortfolio = find.get();

        employeePortfolio.updateExceptBusinessFieldInfo(dto.getIntroduction(),
                dto.getEnrollmentCertificateUrl(), dto.getTranscriptUrl(), dto.getTranscriptUrl(),
                dto.getTopic(), dto.getEnglishTestType(), dto.getEnglishTestScore());

        Employee employee = employeePortfolio.getEmployee();

        if(dto.isPortfolioPublic()){employee.publicizePortfolio();}
        else{employee.privatizePortfolio();}

        // 업종별 정보 수정  그냥 싹 지우고 새로 insert. 수정 판단하기 힘들 것 같음.

        // 삭제
        employeePortfolioBusinessFieldInfoRepository.deleteByEmployeePortfolioId(employeePortfolio.getId());


        // insert
        saveEmployeePortfolioBusinessFieldInfo(employeePortfolio, dto);

    }

    private void clearTemporarySave (Long employeeId) {
        Optional<EmployeePortfolio> find = employeePortfolioRepository.findByEmployeeId(employeeId, TEMPORARY);
        if(find.isEmpty()) {return;}

        EmployeePortfolio employeePortfolio = find.get();

        employeePortfolioRepository.delete(employeePortfolio);

    }


    private void saveEmployeePortfolioBusinessFieldInfo(EmployeePortfolio employeePortfolio, EmployeePortfolioDTO dto){
        List<EmployeePortfolioExperienceDTO> experiences=dto.getExperiences();
        List<EmployeePortfolioCertificationDTO> certifications=dto.getCertifications();
        List<EmployeePortfolioAwardDTO> awards=dto.getAwards();
        List<EmployeePortfolioBusinessFieldInfo> infos=new ArrayList<>();

        for (EmployeePortfolioExperienceDTO experience : experiences) {
            EmployeePortfolioBusinessFieldInfo build = EmployeePortfolioBusinessFieldInfo.builder()
                    .employeePortfolioBusinessFieldType(EXPERIENCE)
                    .businessField(experience.getBusinessField())
                    .content(experience.getExperienceDescription())
                    .startDate(experience.getStartDate())
                    .endDate(experience.getEndDate())
                    .employeePortfolio(employeePortfolio)
                    .build();

            infos.add(build);

        }

        for (EmployeePortfolioCertificationDTO certification : certifications) {
            EmployeePortfolioBusinessFieldInfo build = EmployeePortfolioBusinessFieldInfo.builder()
                    .employeePortfolioBusinessFieldType(CERTIFICATION)
                    .businessField(certification.getBusinessField())
                    .content(certification.getCertificateName())
                    .startDate(certification.getCertificateDate())
                    .employeePortfolio(employeePortfolio)
                    .build();

            infos.add(build);

        }

        for (EmployeePortfolioAwardDTO award : awards) {
            EmployeePortfolioBusinessFieldInfo build = EmployeePortfolioBusinessFieldInfo.builder()
                    .employeePortfolioBusinessFieldType(AWARD)
                    .businessField(award.getBusinessField())
                    .content(award.getAwardName())
                    .startDate(award.getAwardDate())
                    .employeePortfolio(employeePortfolio)
                    .build();

            infos.add(build);

        }



        employeePortfolioBusinessFieldInfoRepository.saveAll(infos);
    }
}
