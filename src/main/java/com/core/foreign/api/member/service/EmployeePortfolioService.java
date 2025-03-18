package com.core.foreign.api.member.service;

import com.core.foreign.api.member.dto.EmployeePortfolioAwardDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioCertificationDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioExperienceDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldInfo;
import com.core.foreign.api.member.repository.EmployeePortfolioBusinessFieldInfoRepository;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.core.foreign.api.member.entity.EmployeePortfolioBusinessFieldType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeePortfolioService {
    private final EmployeePortfolioRepository employeePortfolioRepository;
    private final EmployeePortfolioBusinessFieldInfoRepository employeePortfolioBusinessFieldInfoRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createEmployeePortfolio(Long employeeId, EmployeePortfolioDTO dto){
        Employee employee = (Employee) memberRepository.findById(employeeId).get();

        EmployeePortfolio build = EmployeePortfolio.builder()
                .introduction(dto.getIntroduction())
                .enrollmentCertificateUrl(dto.getEnrollmentCertificateUrl())
                .transcriptUrl(dto.getTranscriptUrl())
                .partTimeWorkPermitUrl(dto.getPartTimeWorkPermitUrl())
                .topik(dto.getTopik())
                .employee(employee)
                .build();

        EmployeePortfolio portfolio = employeePortfolioRepository.save(build);

        saveEmployeePortfolioBusinessFieldInfo(portfolio, dto);

        if(dto.isPortfolioPublic()){employee.publicizePortfolio();}
        else{employee.privatizePortfolio();}

    }


    public EmployeePortfolioDTO getEmployeePortfolio(Long employeeId) {
        EmployeePortfolioDTO response = employeePortfolioRepository.findByEmployeeId(employeeId)
                .map(portfolio -> {
                    Employee employee = portfolio.getEmployee();
                    return EmployeePortfolioDTO.from(portfolio, employee.isPortfolioPublic());
                })
                .orElseGet(EmployeePortfolioDTO::emptyPortfolio);

        return response;
    }


    @Transactional
    public void updateEmployeePortfolio(Long employeeId, EmployeePortfolioDTO dto) {
        Optional<EmployeePortfolio>  find= employeePortfolioRepository.findByEmployeeId(employeeId);
        if(find.isEmpty()) {
            log.warn("[EmployeePortfolioService][updateEmployeePortfolio][왜 없니...][employeeId= {}]", employeeId);

            return;
        }

        EmployeePortfolio employeePortfolio = find.get();

        employeePortfolio.updateExceptBusinessFieldInfo(dto.getIntroduction(),
                dto.getEnrollmentCertificateUrl(), dto.getTranscriptUrl(), dto.getTranscriptUrl(),
                dto.getTopik());

        Employee employee = employeePortfolio.getEmployee();

        if(dto.isPortfolioPublic()){employee.publicizePortfolio();}
        else{employee.privatizePortfolio();}

        // 업종별 정보 수정  그냥 싹 지우고 새로 insert. 수정 판단하기 힘들 것 같음.

        // 삭제
        employeePortfolioBusinessFieldInfoRepository.deleteByEmployeePortfolioId(employeePortfolio.getId());


        // insert
        saveEmployeePortfolioBusinessFieldInfo(employeePortfolio, dto);

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
