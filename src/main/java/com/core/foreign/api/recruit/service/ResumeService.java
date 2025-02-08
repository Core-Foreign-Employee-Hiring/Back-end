package com.core.foreign.api.recruit.service;

import com.core.foreign.api.member.dto.EmployeeBasicResumeResponseDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.PremiumRecruitRepository;
import com.core.foreign.api.recruit.repository.RecruitRepository;
import com.core.foreign.api.recruit.repository.ResumePortfolioRepository;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.core.foreign.api.member.entity.EmployeePortfolioStatus.COMPLETED;
import static com.core.foreign.common.response.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final PremiumRecruitRepository  premiumRecruitRepository;
    private final ResumePortfolioRepository resumePortfolioRepository;
    private final EmployeePortfolioRepository employeePortfolioRepository;




    @Transactional
    public Long applyResume(Long employeeId, Long recruitId, GeneralResumeRequestDTO dto) {
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        if(recruit.getRecruitType()!=RecruitType.GENERAL) {
            throw  new BadRequestException(INVALID_RECRUIT_TYPE_EXCEPTION.getMessage());
        }

        Long l = doApplyResume(employeeId, recruit, dto);
        return l;
    }


    @Transactional
    public Long applyPremiumResume(Long employeeId, Long recruitId, PremiumResumeRequestDTO dto) {


        PremiumRecruit premiumRecruit = premiumRecruitRepository.findPremiumRecruitWithPortfolioById(recruitId)
                .orElseThrow(() -> new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        if(premiumRecruit.getRecruitType()!=RecruitType.PREMIUM) {
            throw  new BadRequestException(INVALID_RECRUIT_TYPE_EXCEPTION.getMessage());
        }

        // 필수인데 작성 하지 않은 것들 예외 처리.
        List<ResumePortfolioRequestDTO> resumePortfolios = dto.getResumePortfolios();

        checkRequiredPortfolio(premiumRecruit, resumePortfolios);


        // 일단 이력서 저장.

        Long resumeId = doApplyResume(employeeId, premiumRecruit, dto.getGeneralResumeRequestDTO());


        // 이력서에 딸린 포트폴리오 저장.

        Resume resume = resumeRepository.findById(resumeId).get();

        List<ResumePortfolio> resumePortfolioEntities = resumePortfolios.stream().map((p) -> p.toEntity(resume)).toList();


        resumePortfolioRepository.saveAll(resumePortfolioEntities);

        return resumeId;
    }


    private void checkRequiredPortfolio(PremiumRecruit premiumRecruit , List<ResumePortfolioRequestDTO> dto) {

        List<Portfolio> portfolios = premiumRecruit.getPortfolios();

        List<Long> requires=new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
            if(portfolio.isRequired()){
                requires.add(portfolio.getId());
            }
        }

        List<Long >requests=new ArrayList<>();
        for (ResumePortfolioRequestDTO dto1 : dto) {
            requests.add(dto1.getPortfolioId());
        }

        // requires 가 requests 의 부분 집합이면 성공.

        if (!new HashSet<>(requests).containsAll(requires)){
            throw new BadRequestException(REQUIRED_PORTFOLIO_MISSING_EXCEPTION.getMessage());
        }


    }


    /**
     * @implNote
     * 해당 공고의 특정 지원자의 이력서를 조회.
     */
    public ApplicationResumeResponseDTO getResume(Long resumeId) {
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruit(resumeId)
                .orElseThrow(() -> {
                    log.error("이력서 없음.");
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        List<ResumePortfolioTestResponseDTO> texts = new ArrayList<>();
        List<ResumePortfolioFileResponseDTO> files = new ArrayList<>();

        // Premium 이면 ResumePortfolio 갖고 와야 함.
        Recruit recruit = resume.getRecruit();
        if(recruit.getRecruitType()==RecruitType.PREMIUM){
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
            employeePortfolioDTO = EmployeePortfolioDTO.from(find.get());
        }else{
            log.warn("완성된 포트폴리오가 없음");
        }



        ApplicationResumeResponseDTO response = new ApplicationResumeResponseDTO(resume.getId(), employeeBasicResumeResponseDTO,employeePortfolioDTO, resume.getMessageToEmployer(), texts, files);
        return response;
    }


    private Long doApplyResume(Long employeeId, Recruit recruit, GeneralResumeRequestDTO dto){
        if(!dto.isThirdPartyConsent()){throw new BadRequestException(THIRD_PARTY_CONSENT_REQUIRED_EXCEPTION.getMessage());}


        Employee employee = (Employee) memberRepository.findById(employeeId).get();

        Resume build = Resume.builder()
                .messageToEmployer(dto.getMessageToEmployer())
                .recruit(recruit)
                .employee(employee)
                .applyMethod(dto.getApplyMethod())
                .recruitmentStatus(RecruitmentStatus.PENDING)
                .evaluationStatus(EvaluationStatus.NOT_EVALUATED)
                .contractStatus(ContractStatus.NOT_WRITTEN)
                .build();


        return resumeRepository.save(build).getId();
    }

    public Page<ApplicationResumePreviewResponseDTO> searchApplicationResume(Long recruitId,
                                                                             String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                                                             Integer page){
        Pageable pageable = PageRequest.of(page, 5);

        Page<Resume> resumes = resumeRepository.searchResumedByRecruitId(recruitId, keyword, recruitmentStatus, contractStatus, pageable);
        Page<ApplicationResumePreviewResponseDTO> response = resumes.map(ApplicationResumePreviewResponseDTO::from);
        return response;
    }



    @Transactional
    public void rejectResume(Long resumeId){
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage()));
        resume.reject();
    }

    @Transactional
    public void approveResume(Long resumeId){
        Resume resume = resumeRepository.findByResumeIdWithRecruit(resumeId)
                .orElseThrow(() -> {
                    log.error("이력서 없음 resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        Recruit recruit = resume.getRecruit();

        if(resume.getRecruitmentStatus().equals(RecruitmentStatus.APPROVED)){
            log.error("이미 승인된 이력서 resumeId= {}", resumeId);
            throw new BadRequestException(ALREADY_APPROVED_RESUME_EXCEPTION.getMessage());
        }

        if(resume.getRecruitmentStatus().equals(RecruitmentStatus.REJECTED)){
            log.error("이미 거절된 이력서 resumeId= {}", resumeId);
            throw new BadRequestException(ALREADY_REJECTED_RESUME_EXCEPTION.getMessage());
        }


        if(recruit.getRecruitCount()<=recruit.getCurrentRecruitCount()){
            throw new BadRequestException(EXCEEDED_RECRUIT_CAPACITY_EXCEPTION.getMessage());
        }

        recruit.increaseCurrentRecruitCount();
        resume.approve();
    }


    public Page<EmployeeApplicationStatusResponseDTO> getMyResumes(Long employeeId, Integer page){
        Pageable pageable= PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "id"));
        Page<EmployeeApplicationStatusResponseDTO> response = resumeRepository.findResumeByEmployeeId(employeeId, pageable)
                .map(EmployeeApplicationStatusResponseDTO::from);

        return response;
    }



    @Transactional
    public void removeMyResume(Long employeeId, Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruit(resumeId)
                .orElseThrow(() -> {
                    log.error("이력서 없음. resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        Employee employee = resume.getEmployee();

        if(!Objects.equals(employeeId, employee.getId())){
            log.error("다름 사람 이력서 삭세 시도.");
            throw new BadRequestException(UNAUTHORIZED_RESUME_DELETE_EXCEPTION.getMessage());
        }


        resumeRepository.delete(resume);
    }

}
