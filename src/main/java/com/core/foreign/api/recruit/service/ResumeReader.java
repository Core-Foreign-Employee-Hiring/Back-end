package com.core.foreign.api.recruit.service;

import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.file.dto.FileUrlAndOriginalFileNameDTO;
import com.core.foreign.api.file.entity.UploadFile;
import com.core.foreign.api.file.repository.UploadFileRepository;
import com.core.foreign.api.member.dto.TagResponseDTO;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.dto.internal.ResumeDTO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.core.foreign.common.response.ErrorStatus.RESUME_NOT_FOUND_EXCEPTION;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeReader {
    private final ResumeRepository resumeRepository;
    private final ResumePortfolioRepository resumePortfolioRepository;
    private final PortfolioRepository portfolioRepository;
    private final UploadFileRepository uploadFileRepository;

    public ResumeDTO getResumeForEmployer(Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruitForEmployer(resumeId)
                .orElseThrow(() -> {
                    log.warn("[getResumeForEmployer][이력서 없음.][resumeId= {}]", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        ResumePortfolioDTO resumePortfolio = getResumePortfolio(resume);

        ResumeDTO response = ResumeDTO.from(resume, resumePortfolio);

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

    public ResumeDTO getMyResume(Long employeeId, Long resumeId){
        Resume resume = resumeRepository.findMyResumeWithEmployeeAndRecruit(employeeId, resumeId)
                .orElseThrow(() -> {
                    log.warn("[getResume][이력서 없음.][employeeId= {}, resumeId= {}]", employeeId, resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        ResumePortfolioDTO resumePortfolio = getResumePortfolio(resume);

        ResumeDTO response = ResumeDTO.from(resume, resumePortfolio);

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

                List<UploadFile> byFileUrls = uploadFileRepository.findByFileUrls(urls);

                List<FileUrlAndOriginalFileNameDTO> fileUrlAndOriginalFileNameDTOS =new ArrayList<>();

                for (UploadFile byFileUrl : byFileUrls) {
                    FileUrlAndOriginalFileNameDTO fileUrlAndOriginalFileNameDTO = new FileUrlAndOriginalFileNameDTO(byFileUrl.getFileUrl(), byFileUrl.getOriginalFileName());
                    fileUrlAndOriginalFileNameDTOS.add(fileUrlAndOriginalFileNameDTO);
                }

                files.add(new ResumePortfolioFileResponseDTO(portfolio.getTitle(), fileUrlAndOriginalFileNameDTOS));
            }
        }

        ResumePortfolioDTO response = ResumePortfolioDTO.from(texts, files);

        return response;
    }

}
