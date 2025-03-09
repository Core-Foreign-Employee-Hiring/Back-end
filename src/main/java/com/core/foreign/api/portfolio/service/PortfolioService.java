package com.core.foreign.api.portfolio.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.repository.EmployeeRepository;
import com.core.foreign.api.member.repository.EmployerEmployeeRepository;
import com.core.foreign.api.member.repository.EmployerResumeRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.service.EvaluationReader;
import com.core.foreign.api.portfolio.dto.internal.BasicPortfolioDTO;
import com.core.foreign.api.portfolio.dto.response.ApplicationPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.response.ApplicationPortfolioResponseDTO;
import com.core.foreign.api.portfolio.dto.response.BasicPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.response.BasicPortfolioResponseDTO;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioFileResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioTextResponseDTO;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.core.foreign.common.response.ErrorStatus.RESUME_NOT_FOUND_EXCEPTION;
import static com.core.foreign.common.response.ErrorStatus.USER_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final EmployeeRepository employeeRepository;
    private final EvaluationReader evaluationReader;
    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;
    private final EmployerEmployeeRepository employerEmployeeRepository;
    private final EmployerResumeRepository employerResumeRepository;
    private final MemberRepository memberRepository;
    private final PortfolioReader portfolioReader;

    public PageResponseDTO<BasicPortfolioPreviewResponseDTO> getBasicPortfolios(Integer page, Integer size) {
        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Employee> by = employeeRepository.findAllBy(pageable);

        Map<Long, EmployeeEvaluationCountDTO> employeeEvaluations = evaluationReader.getEmployeeEvaluations(by.getContent());

        Page<BasicPortfolioPreviewResponseDTO> dto = by
                .map((employee -> {
                    EmployeeEvaluationCountDTO employeeEvaluationCountDTO = employeeEvaluations.get(employee.getId());

                    return new BasicPortfolioPreviewResponseDTO(employee, employeeEvaluationCountDTO);
                }));

        PageResponseDTO<BasicPortfolioPreviewResponseDTO> response = PageResponseDTO.of(dto);

        return response;

    }

    public PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> getApplicationPortfolios(Integer page, Integer size, List<BusinessField> field) {
        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Resume> applicationPortfolio = resumeRepository.getApplicationPortfolio(field, pageable);

        // 평가 갖고 온다.
        List<Employee> employees = applicationPortfolio.map(Resume::getEmployee).toList();
        Map<Long, EmployeeEvaluationCountDTO> employeeEvaluations = evaluationReader.getEmployeeEvaluations(employees);

        Page<ApplicationPortfolioPreviewResponseDTO> dto = applicationPortfolio.map((resume) -> {
            Employee employee = resume.getEmployee();
            Recruit recruit = resume.getRecruit();

            EmployeeEvaluationCountDTO employeeEvaluationCountDTO = employeeEvaluations.get(employee.getId());
            List<BusinessField> businessFields = recruit.getBusinessFields().stream().toList();

            return new ApplicationPortfolioPreviewResponseDTO(resume, employeeEvaluationCountDTO, businessFields);

        });

        PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> response = PageResponseDTO.of(dto);

        return response;

    }

    @Transactional
    public BasicPortfolioResponseDTO getBasicPortfolio(Long employerId, Long employeeId) {
        Employee employee = employeeRepository.findPublicEmployeeById(employeeId)
                .orElseThrow(() -> {
                    log.error("피고용인 찾을 수 없음. employeeId= {}", employeeId);
                    return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                });

        BasicPortfolioDTO basicPortfolio = portfolioReader.getBasicPortfolio(employee);

        EmployeeEvaluationCountDTO employeeEvaluation = evaluationReader.getEmployeeEvaluation(employee);

        BasicPortfolioResponseDTO response = BasicPortfolioResponseDTO.from(basicPortfolio, employeeEvaluation);

        Integer viewCount = employee.getViewCount();
        response.setViewCount(viewCount);

        if(employerId!=null && employerEmployeeRepository.existsByEmployerIdAndEmployeeId(employerId, employeeId)){
            response.like();
        }

        employeeRepository.increaseViewCount(employeeId);

        return response;
    }

    @Transactional
    public ApplicationPortfolioResponseDTO getApplicationPortfolio(Long employerId, Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruitForPortfolio(resumeId)
                .orElseThrow(() -> {
                    log.warn("[getResume][이력서 없음.][resumeId= {}]", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        Employee employee = resume.getEmployee();

        BasicPortfolioDTO basicPortfolio = portfolioReader.getBasicPortfolio(employee);
        ResumePortfolioDTO resumePortfolio = resumeReader.getResumePortfolio(resume);

        List<ResumePortfolioTextResponseDTO> texts = resumePortfolio.getTexts();
        List<ResumePortfolioFileResponseDTO> files = resumePortfolio.getFiles();

        Integer viewCount = resume.getViewCount()+1;
        resumeRepository.increaseViewCount(resumeId);

        EmployeeEvaluationCountDTO employeeEvaluation = evaluationReader.getEmployeeEvaluation(employee);

        ApplicationPortfolioResponseDTO response = new ApplicationPortfolioResponseDTO(resumeId, basicPortfolio, texts, files, viewCount, employeeEvaluation);

        if(employerResumeRepository.existsByEmployerIdAndResumeId(employerId, resumeId)){
            response.like();
        }

        return response;
    }

    @Transactional
    public boolean flipEmployerEmployee(Long employerId, Long employeeId) {
        Optional<EmployerEmployee> findEmployerEmployee = employerEmployeeRepository.findByEmployerIdAndEmployeeId(employerId, employeeId);

        if (findEmployerEmployee.isPresent()) {
            EmployerEmployee recruitBookmark = findEmployerEmployee.get();
            employerEmployeeRepository.delete(recruitBookmark);
            return false;
        } else {
            Employer employer = (Employer) memberRepository.findByMemberIdAndRole(employerId, Role.EMPLOYER)
                    .orElseThrow(() -> {
                        log.warn("[flipEmployerEmployee][고용인 찾을 수 없음.][employerId= {}]", employerId);
                        return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                    });

            Employee employee = (Employee) memberRepository.findByMemberIdAndRole(employeeId, Role.EMPLOYEE)
                    .orElseThrow(() -> {
                        log.warn("[flipEmployerEmployee][피고용인 찾을 수 없음.][employeeId= {}]", employeeId);
                        return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                    });

            EmployerEmployee employerEmployee = new EmployerEmployee(employer, employee);
            employerEmployeeRepository.save(employerEmployee);
            return true;
        }
    }

    @Transactional
    public boolean flipEmployerResume(Long employerId, Long resumeId) {
        Optional<EmployerResume> findEmployerResume = employerResumeRepository.findByEmployerIdAndResumeId(employerId, resumeId);

        if (findEmployerResume.isPresent()) {
            EmployerResume employerResume = findEmployerResume.get();
            employerResumeRepository.delete(employerResume);
            return false;
        } else {
            Employer employer = (Employer) memberRepository.findByMemberIdAndRole(employerId, Role.EMPLOYER)
                    .orElseThrow(() -> {
                        log.warn("[flipEmployerResume][고용인 찾을 수 없음.][employerId= {}]", employerId);
                        return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                    });

            Resume resume = resumeRepository.findById(resumeId)
                    .orElseThrow(() -> {
                        log.warn("[flipEmployerResume][이력서 찾을 수 없음.][resumeId= {}]", resumeId);
                        return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                    });

            EmployerResume employerResume = new EmployerResume(employer, resume);
            employerResumeRepository.save(employerResume);
            return true;
        }
    }
}
