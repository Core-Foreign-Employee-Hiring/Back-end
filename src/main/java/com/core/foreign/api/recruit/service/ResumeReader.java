package com.core.foreign.api.recruit.service;

import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.member.dto.EmployeeBasicResumeResponseDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.TagResponseDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.PortfolioRepository;
import com.core.foreign.api.recruit.repository.ResumePortfolioRepository;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.core.foreign.api.member.entity.EmployeePortfolioStatus.COMPLETED;
import static com.core.foreign.common.response.ErrorStatus.RESUME_NOT_FOUND_EXCEPTION;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeReader {
    private final ResumeRepository resumeRepository;
    private final ResumePortfolioRepository resumePortfolioRepository;
    private final EmployeePortfolioRepository employeePortfolioRepository;
    private final PortfolioRepository portfolioRepository;


    public ApplicationResumeResponseDTO getResume(Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruit(resumeId)
                .orElseThrow(() -> {
                    log.warn("[getResume][이력서 없음.][resumeId= {}]", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        ResumePortfolioDTO resumePortfolio = getResumePortfolio(resume);

        List<ResumePortfolioFileResponseDTO> files = resumePortfolio.getFiles();
        List<ResumePortfolioTextResponseDTO> texts = resumePortfolio.getTexts();

        Employee employee = resume.getEmployee();

        EmployeeBasicResumeResponseDTO employeeBasicResumeResponseDTO = EmployeeBasicResumeResponseDTO.from(employee);
        Optional<EmployeePortfolio> find= employeePortfolioRepository.findByEmployeeId(employee.getId(), COMPLETED);
        EmployeePortfolioDTO employeePortfolioDTO=null;
        if(find.isPresent()){
            employeePortfolioDTO = EmployeePortfolioDTO.from(find.get(), employee.isPortfolioPublic());
        }else{
            log.warn("[getResume][완성된 포트폴리오가 없음]");
        }

        ApplicationResumeResponseDTO response = new ApplicationResumeResponseDTO(resume, employeeBasicResumeResponseDTO,employeePortfolioDTO, resume.getMessageToEmployer(), texts, files);

        return response;
    }

    public PageResponseDTO<ApplicationResumePreviewResponseDTO> searchApplicationResume(Long recruitId,
                                                                                        String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                                                                        Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Resume> resumes = resumeRepository.searchResumeByRecruitId(recruitId, keyword, recruitmentStatus, contractStatus, pageable);
        Page<ApplicationResumePreviewResponseDTO> dto = resumes.map(ApplicationResumePreviewResponseDTO::from);

        PageResponseDTO<ApplicationResumePreviewResponseDTO> response = PageResponseDTO.of(dto);
        return response;
    }

    public PageResponseDTO<EmployeeApplicationStatusResponseDTO> getMyResumes(Long employeeId, Integer page, Integer size){
        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<EmployeeApplicationStatusResponseDTO> dto = resumeRepository.findResumeByEmployeeId(employeeId, pageable)
                .map(EmployeeApplicationStatusResponseDTO::from);

        PageResponseDTO<EmployeeApplicationStatusResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }


    public PageResponseDTO<TagResponseDTO> getTags(Long employerId, EvaluationStatus evaluationStatus, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<TagResponseDTO> dto = resumeRepository.findResumeByEmployeeIdAndEvaluationStatus(employerId, evaluationStatus, pageable)
                .map(TagResponseDTO::from);

        PageResponseDTO<TagResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }


    public ResumePortfolioDTO getResumePortfolio(Resume resume){
        List<ResumePortfolioTextResponseDTO> texts = new ArrayList<>();
        List<ResumePortfolioFileResponseDTO> files = new ArrayList<>();

        Recruit recruit = resume.getRecruit();
        List<Portfolio> byRecruitId = portfolioRepository.findByRecruitId(recruit.getId());
        Map<Long,  Portfolio> map=new HashMap<>();
        for (Portfolio portfolio : byRecruitId) {
            map.put(portfolio.getId(), portfolio);
        }

        if(recruit.getRecruitType()== RecruitType.PREMIUM){
            List<ResumePortfolio> resumePortfolios = resumePortfolioRepository.findByResumeId(resume.getId());


            /**
             * ResumePortfolio 에 외래키 제약 조건을 추가 고려.
             * title 로 할 시 데이터 일관성 불안함.
             */
            Map<Long ,List<String>> fileMap=new HashMap<>();  // key: portfolioId, value: urls

            for (ResumePortfolio resumePortfolio : resumePortfolios) {
                PortfolioType portfolioType = resumePortfolio.getPortfolioType();
                Long recruitPortfolioId = resumePortfolio.getRecruitPortfolioId();
                String content = resumePortfolio.getContent();

                if(portfolioType==PortfolioType.FILE_UPLOAD){
                    if (fileMap.containsKey(recruitPortfolioId)) {
                        fileMap.get(recruitPortfolioId).add(content);
                    } else {
                        fileMap.put(recruitPortfolioId, new ArrayList<>(List.of(content)));
                    }

                }
                else if(portfolioType==PortfolioType.LONG_TEXT || portfolioType==PortfolioType.SHORT_TEXT){
                    texts.add(ResumePortfolioTextResponseDTO.from(resumePortfolio));
                }
            }

            for (Long recruitPortfolioId : fileMap.keySet()) {
                List<String> urls = fileMap.get(recruitPortfolioId);
                Portfolio portfolio = map.get(recruitPortfolioId);

                files.add(new ResumePortfolioFileResponseDTO(PortfolioType.FILE_UPLOAD, portfolio.getTitle(), urls));
            }
        }

        ResumePortfolioDTO response = ResumePortfolioDTO.from(texts, files);

        return response;
    }

}
