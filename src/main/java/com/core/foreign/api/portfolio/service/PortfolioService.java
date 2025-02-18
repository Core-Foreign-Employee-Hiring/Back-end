package com.core.foreign.api.portfolio.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.entity.EmployeePortfolioStatus;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.member.repository.EmployeeRepository;
import com.core.foreign.api.member.service.EvaluationReader;
import com.core.foreign.api.portfolio.dto.ApplicationPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.ApplicationPortfolioResponseDTO;
import com.core.foreign.api.portfolio.dto.BasicPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.BasicPortfolioResponseDTO;
import com.core.foreign.api.recruit.dto.ApplicationResumeResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioFileResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioTestResponseDTO;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.api.recruit.service.ResumeReader;
import com.core.foreign.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.core.foreign.common.response.ErrorStatus.USER_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final EmployeeRepository employeeRepository;
    private final EvaluationReader evaluationReader;
    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;
    private final EmployeePortfolioRepository employeePortfolioRepository;



    public Page<BasicPortfolioPreviewResponseDTO> getBasicPortfolios(Integer page) {
        Pageable pageable= PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Employee> by = employeeRepository.findAll(pageable);


        Map<Long, EmployeeEvaluationCountDTO> employeeEvaluations = evaluationReader.getEmployeeEvaluations(by.getContent());

        Page<BasicPortfolioPreviewResponseDTO> response = by
                .map((employee -> {
                    EmployeeEvaluationCountDTO employeeEvaluationCountDTO = employeeEvaluations.get(employee.getId());

                    return new BasicPortfolioPreviewResponseDTO(employee, employeeEvaluationCountDTO);
                }));

        return response;

    }

    /**
     * 필터는 공고 부분 업직종 수정 후 가능함.
     */
    public Page<ApplicationPortfolioPreviewResponseDTO> getApplicationPortfolios(Integer page, BusinessField field) {
        Pageable pageable= PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Resume> applicationPortfolio = resumeRepository.getApplicationPortfolio(field, pageable);

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

        Page<ApplicationPortfolioPreviewResponseDTO> response = applicationPortfolio.map((resume) -> {
            Employee employee = resume.getEmployee();
            Recruit recruit = resume.getRecruit();

            EmployeeEvaluationCountDTO employeeEvaluationCountDTO = employeeEvaluations.get(employee.getId());
            List<BusinessField> businessFields = businessMap.get(recruit);

            return new ApplicationPortfolioPreviewResponseDTO(resume, employeeEvaluationCountDTO, businessFields);

        });

        return response;

    }


    public BasicPortfolioResponseDTO getBasicPortfolio(Long employeeId) {
        Employee employee = employeeRepository.findPublicEmployeeById(employeeId)
                .orElseThrow(() -> {
                    log.error("피고용인 찾을 수 없음. employeeId= {}", employeeId);
                    return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                });

        EmployeePortfolio employeePortfolio = employeePortfolioRepository.findByEmployeeId(employeeId, EmployeePortfolioStatus.COMPLETED)
                .orElseGet(() -> {
                    log.error("완성된 포트 폴리오가 없음. employeeId= {}", employeeId);
                    return null;
                });


        EmployeeEvaluationCountDTO employeeEvaluation = evaluationReader.getEmployeeEvaluation(employee);

        BasicPortfolioResponseDTO response = BasicPortfolioResponseDTO.from(employee, employeePortfolio, employeeEvaluation);

        return response;
    }

    @Transactional(readOnly = true)
    public ApplicationPortfolioResponseDTO getApplicationPortfolio(Long resumeId){
        ApplicationResumeResponseDTO resume = resumeReader.getResume(resumeId);
        Long employeeId = resume.getEmployeeId();

        BasicPortfolioResponseDTO basicPortfolio = getBasicPortfolio(employeeId);
        EmployeePortfolioDTO employeePortfolioDTO = resume.getEmployeePortfolioDTO();
        List<ResumePortfolioTestResponseDTO> texts = resume.getTexts();
        List<ResumePortfolioFileResponseDTO> files = resume.getFiles();

        ApplicationPortfolioResponseDTO response = new ApplicationPortfolioResponseDTO(resumeId, basicPortfolio, employeePortfolioDTO, texts, files);

        return response;
    }


}
