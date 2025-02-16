package com.core.foreign.api.recruit.service;

import com.core.foreign.api.member.dto.EmployeeBasicResumeResponseDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.TagResponseDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
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


    public ApplicationResumeResponseDTO getResume(Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruit(resumeId)
                .orElseThrow(() -> {
                    log.error("이력서 없음. resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        List<ResumePortfolioTestResponseDTO> texts = new ArrayList<>();
        List<ResumePortfolioFileResponseDTO> files = new ArrayList<>();

        // Premium 이면 ResumePortfolio 갖고 와야 함.
        Recruit recruit = resume.getRecruit();
        if(recruit.getRecruitType()== RecruitType.PREMIUM){
            List<ResumePortfolio> resumePortfolios = resumePortfolioRepository.findByResumeId(resume.getId());


            /**
             * ResumePortfolio 에 외래키 제약 조건을 추가 고려.
             * title 로 할 시 데이터 일관성 불안함.
             */
            Map<String ,List<String>> fileMap=new HashMap<>();  // key: title, value: urls

            for (ResumePortfolio resumePortfolio : resumePortfolios) {
                PortfolioType portfolioType = resumePortfolio.getPortfolioType();
                String title = resumePortfolio.getTitle();
                String content = resumePortfolio.getContent();

                if(portfolioType==PortfolioType.FILE_UPLOAD){
                    if (fileMap.containsKey(title)) {
                        fileMap.get(title).add(content);
                    } else {
                        fileMap.put(title, new ArrayList<>(List.of(content)));
                    }

                }
                else if(portfolioType==PortfolioType.LONG_TEXT || portfolioType==PortfolioType.SHORT_TEXT){
                    texts.add(ResumePortfolioTestResponseDTO.from(resumePortfolio));
                }
            }

            for (String title : fileMap.keySet()) {
                List<String> urls = fileMap.get(title);

                files.add(new ResumePortfolioFileResponseDTO(PortfolioType.FILE_UPLOAD, title, urls));
            }
        }

        Employee employee = resume.getEmployee();

        EmployeeBasicResumeResponseDTO employeeBasicResumeResponseDTO = EmployeeBasicResumeResponseDTO.from(employee);
        Optional<EmployeePortfolio> find= employeePortfolioRepository.findByEmployeeId(employee.getId(), COMPLETED);
        EmployeePortfolioDTO employeePortfolioDTO=null;
        if(find.isPresent()){
            employeePortfolioDTO = EmployeePortfolioDTO.from(find.get(), employee.isPortfolioPublic());
        }else{
            log.warn("완성된 포트폴리오가 없음");
        }

        ApplicationResumeResponseDTO response = new ApplicationResumeResponseDTO(resume, employeeBasicResumeResponseDTO,employeePortfolioDTO, resume.getMessageToEmployer(), texts, files);

        return response;
    }

    public Page<ApplicationResumePreviewResponseDTO> searchApplicationResume(Long recruitId,
                                                                             String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                                                             Integer page){
        Pageable pageable = PageRequest.of(page, 5);

        Page<Resume> resumes = resumeRepository.searchResumedByRecruitId(recruitId, keyword, recruitmentStatus, contractStatus, pageable);
        Page<ApplicationResumePreviewResponseDTO> response = resumes.map(ApplicationResumePreviewResponseDTO::from);
        return response;
    }

    public Page<EmployeeApplicationStatusResponseDTO> getMyResumes(Long employeeId, Integer page){
        Pageable pageable= PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "id"));
        Page<EmployeeApplicationStatusResponseDTO> response = resumeRepository.findResumeByEmployeeId(employeeId, pageable)
                .map(EmployeeApplicationStatusResponseDTO::from);

        return response;
    }


    public Page<TagResponseDTO> getTags(Long employerId, EvaluationStatus evaluationStatus, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<TagResponseDTO> response = resumeRepository.findResumeByEmployeeIdAndEvaluationStatus(employerId, evaluationStatus, pageable)
                .map(TagResponseDTO::from);


        return response;
    }


}
