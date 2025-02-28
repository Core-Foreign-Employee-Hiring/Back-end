package com.core.foreign.api.recruit.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.dto.EmployerEvaluationCountDTO;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.service.EvaluationReader;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.*;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.core.foreign.common.response.ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final PremiumManageRepository premiumManageRepository;
    private final S3Service s3Service;
    private final ResumeRepository resumeRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    private final EvaluationReader evaluationReader;
    private final PortfolioRepository portfolioRepository;

    // 일반 공고 등록
    @Transactional
    public void createGeneralRecruit(
            Long memberId,
            RecruitRequestDTO.GeneralRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);
        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        GeneralRecruit generalRecruit = GeneralRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.PUBLISHED)
                .jumpDate(null)
                .build();

        recruitRepository.save(generalRecruit);
    }

    // 프리미엄 공고 등록
    @Transactional
    public void createPremiumRecruit(
            Long memberId,
            RecruitRequestDTO.PremiumRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);

        // 프리미엄 공고 등록 가능 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }
        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit premiumRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.PUBLISHED)
                .jumpDate(null)
                .build();

        List<Portfolio> portfolios = request.getPortfolios().stream()
                .map(p -> Portfolio.builder()
                        .title(p.getTitle())
                        .type(p.getType())
                        .isRequired(p.isRequired())
                        .maxFileCount(p.getMaxFileCount())
                        .build())
                .toList();

        portfolios.forEach(premiumRecruit::addPortfolio);

        // 프리미엄 공고 등록 횟수 감소
        int updatedRows = premiumManageRepository.decreasePremiumCount(employer.getId());
        if (updatedRows == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }

        recruitRepository.save(premiumRecruit);
    }

    // 일반 공고 임시저장
    @Transactional
    public void createGeneralDraft(Long memberId, RecruitRequestDTO.GeneralRecruitRequest request, MultipartFile posterImage) {

        Member employer = getEmployer(memberId);

        String posterImageUrl = uploadPosterImage(posterImage);

        GeneralRecruit draft = GeneralRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(request.getBusinessFields() != null ? new java.util.HashSet<>(request.getBusinessFields()) : null)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(request.getPreferredConditions())
                .workDuration(request.getWorkDuration())
                .workDurationOther(request.getWorkDurationOther())
                .workTime(request.getWorkTime())
                .workTimeOther(request.getWorkTimeOther())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(request.getApplicationMethods() != null ? new java.util.HashSet<>(request.getApplicationMethods()) : null)
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.DRAFT)
                .jumpDate(null)
                .build();
        recruitRepository.save(draft);
    }

    // 임시 저장된 일반 공고 수정
    @Transactional
    public void updateGeneralDraft(Long memberId, Long draftId, RecruitRequestDTO.GeneralRecruitRequest request, MultipartFile posterImage) {
        // 공고를 찾을 수 없을 경우 예외처리
        GeneralRecruit draft = recruitRepository.findGeneralDraftById(draftId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        // 원 작성자와 요청 사용자가 다를경우 예외처리
        if (!draft.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.ONLY_MODIFY_WRITER_USER_EXCEPTION.getMessage());
        }

        String posterImageUrl = uploadPosterImage(posterImage);
        draft.updateFrom(request, posterImageUrl);
        recruitRepository.save(draft);
    }

    // 일반 공고 퍼블리싱
    @Transactional
    public void publishGeneralDraft(Long memberId, Long draftId, RecruitRequestDTO.GeneralRecruitRequest request, MultipartFile posterImage) {
        // 공고를 찾을 수 없을 경우 예외처리
        GeneralRecruit draft = recruitRepository.findGeneralDraftById(draftId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        // 원 작성자와 요청 사용자가 다를경우 예외처리
        if (!draft.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.ONLY_MODIFY_WRITER_USER_EXCEPTION.getMessage());
        }

        // 이미 퍼블리싱인 경우 예외처리
        if (!draft.getRecruitPublishStatus().equals(RecruitPublishStatus.DRAFT)) {
            throw new BadRequestException(ErrorStatus.ALEADY_PUBLISHED_RECRUIT_ARTICLE_EXCEPTION.getMessage());
        }

        String posterImageUrl = uploadPosterImage(posterImage);
        draft.updateFrom(request, posterImageUrl);
        draft.updatePublishStatus(RecruitPublishStatus.PUBLISHED);
        recruitRepository.save(draft);
    }

    // 프리미엄 공고 임시저장
    @Transactional
    public void savePremiumRecruitDraft(
            Long memberId,
            RecruitRequestDTO.PremiumRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);

        // 프리미엄 공고 등록 가능 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit draft = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(request.getBusinessFields() != null ? new java.util.HashSet<>(request.getBusinessFields()) : null)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(request.getPreferredConditions())
                .workDuration(request.getWorkDuration())
                .workDurationOther(request.getWorkDurationOther())
                .workTime(request.getWorkTime())
                .workTimeOther(request.getWorkTimeOther())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(request.getApplicationMethods() != null ? new java.util.HashSet<>(request.getApplicationMethods()) : null)
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.DRAFT)
                .jumpDate(null)
                .build();
        // 포트폴리오 저장
        if(request.getPortfolios() != null) {
            for (RecruitRequestDTO.PremiumRecruitRequest.PortfolioRequest dto : request.getPortfolios()) {
                Portfolio portfolio = Portfolio.builder()
                        .title(dto.getTitle())
                        .type(dto.getType())
                        .isRequired(dto.isRequired())
                        .maxFileCount(dto.getMaxFileCount())
                        .build();
                draft.addPortfolio(portfolio);
            }
        }
        recruitRepository.save(draft);
    }

    // 임시 저장된 프리미엄 공고 수정
    @Transactional
    public void updatePremiumDraft(Long memberId, Long draftId, RecruitRequestDTO.PremiumRecruitRequest request, MultipartFile posterImage) {
        // 공고를 찾을 수 없을 경우 예외처리
        PremiumRecruit draft = recruitRepository.findPremiumDraftById(draftId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        // 원 작성자와 요청 사용자가 다를경우 예외처리
        if (!draft.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.ONLY_MODIFY_WRITER_USER_EXCEPTION.getMessage());
        }

        String posterImageUrl = uploadPosterImage(posterImage);
        draft.updateFrom(request, posterImageUrl);
        draft.updatePortfolios(request.getPortfolios());
        recruitRepository.save(draft);
    }

    // 프리미엄 공고 퍼블리싱
    @Transactional
    public void publishPremiumDraft(Long memberId, Long draftId, RecruitRequestDTO.PremiumRecruitRequest request, MultipartFile posterImage) {
        // 공고를 찾을 수 없을 경우 예외처리
        PremiumRecruit draft = recruitRepository.findPremiumDraftById(draftId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        // 원 작성자와 요청 사용자가 다를경우 예외처리
        if (!draft.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.ONLY_MODIFY_WRITER_USER_EXCEPTION.getMessage());
        }

        // 이미 퍼블리싱인 경우 예외처리
        if (!draft.getRecruitPublishStatus().equals(RecruitPublishStatus.DRAFT)) {
            throw new BadRequestException(ErrorStatus.ALEADY_PUBLISHED_RECRUIT_ARTICLE_EXCEPTION.getMessage());
        }

        // 프리미엄 공고 등록 가능 여부 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(draft.getEmployer().getId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));
        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }

        String posterImageUrl = uploadPosterImage(posterImage);

        draft.updateFrom(request, posterImageUrl);
        draft.updatePortfolios(request.getPortfolios());
        draft.updatePublishStatus(RecruitPublishStatus.PUBLISHED);

        // 프리미엄 등록 횟수 차감
        int updatedRows = premiumManageRepository.decreasePremiumCount(draft.getEmployer().getId());
        if (updatedRows == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }

        recruitRepository.save(draft);
    }

    // 사용자 임시저장 공고 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean hasDrafts(Long memberId) {
        Member employer = getEmployer(memberId);
        return recruitRepository.existsByEmployerAndRecruitPublishStatus(employer, RecruitPublishStatus.DRAFT);
    }

    // 임시 저장 공고 데이터 조회
    @Transactional(readOnly = true)
    public RecruitResponseDTO getDraftById(Long recruitId, Long memberId) {
        Recruit draft = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
        if (!RecruitPublishStatus.DRAFT.equals(draft.getRecruitPublishStatus())) {
            throw new BadRequestException(ErrorStatus.NOT_DRAFT_RECRUIT_EXCEPTION.getMessage());
        }
        if (!draft.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.INVALID_USER_EXCEPTION.getMessage());
        }
        return convertDraftToResponseDTO(draft);
    }

    private RecruitResponseDTO convertDraftToResponseDTO(Recruit draft) {
        List<RecruitResponseDTO.PortfolioDTO> portfolioDTOs = new ArrayList<>();
        if (draft instanceof PremiumRecruit premiumRecruit) {
            portfolioDTOs = premiumRecruit.getPortfolios().stream()
                    .map(portfolio -> RecruitResponseDTO.PortfolioDTO.builder()
                            .title(portfolio.getTitle())
                            .type(portfolio.getType())
                            .isRequired(portfolio.isRequired())
                            .maxFileCount(portfolio.getMaxFileCount())
                            .build())
                    .toList();
        }
        return RecruitResponseDTO.builder()
                .id(draft.getId())
                .title(draft.getTitle())
                .address(draft.getAddress())
                .recruitType(draft.getRecruitType())
                .recruitStartDate(draft.getRecruitStartDate())
                .recruitEndDate(draft.getRecruitEndDate())
                .gender(draft.getGender())
                .education(draft.getEducation())
                .otherConditions(draft.getOtherConditions())
                .preferredConditions(new ArrayList<>(draft.getPreferredConditions()))
                .workDuration(new ArrayList<>(draft.getWorkDuration()))
                .workDurationOther(draft.getWorkDurationOther())
                .workTime(new ArrayList<>(draft.getWorkTime()))
                .workTimeOther(draft.getWorkTimeOther())
                .workDays(new ArrayList<>(draft.getWorkDays()))
                .workDaysOther(draft.getWorkDaysOther())
                .salary(draft.getSalary())
                .salaryType(draft.getSalaryType())
                .salaryOther(draft.getSalaryOther())
                .businessFields(new HashSet<>(draft.getBusinessFields()))
                .applicationMethods(new HashSet<>(draft.getApplicationMethods()))
                .posterImageUrl(draft.getPosterImageUrl())
                .latitude(draft.getLatitude())
                .longitude(draft.getLongitude())
                .portfolios(portfolioDTOs)
                .build();
    }

    // 사용자 조회
    private Member getEmployer(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));
    }

    // 기존 임시 저장 데이터 삭제
    @Transactional
    protected void deleteDraft(Member employer) {
        recruitRepository.findAllByEmployerAndRecruitPublishStatus(employer, RecruitPublishStatus.DRAFT)
                .ifPresent(recruitRepository::delete);
    }

    // 포스터 이미지 업로드
    private String uploadPosterImage(MultipartFile posterImage) {
        if (posterImage != null && !posterImage.isEmpty()) {
            try {
                return s3Service.uploadRecruitPostImage(posterImage);
            } catch (IOException e) {
                throw new InternalServerException(ErrorStatus.FAIL_UPLOAD_EXCEPTION.getMessage());
            }
        }
        return null;
    }

    // 공고 상태 확인
    private void validateDraftStatus(Recruit recruit) {
        if (!RecruitPublishStatus.DRAFT.equals(recruit.getRecruitPublishStatus())) {
            throw new BadRequestException(ErrorStatus.ALEADY_PUBLISHED_RECRUIT_ARTICLE_EXCEPTION.getMessage());
        }
    }

    // 일반 공고 조회
    private GeneralRecruit getGeneralRecruit(Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
        if (!(recruit instanceof GeneralRecruit)) {
            throw new BadRequestException(ErrorStatus.NOT_GENERAL_RECRUIT_EXCEPTION.getMessage());
        }
        return (GeneralRecruit) recruit;
    }

    // 프리미엄 공고 조회
    private PremiumRecruit getPremiumRecruit(Long recruitId) {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
        if (!(recruit instanceof PremiumRecruit)) {
            throw new BadRequestException(ErrorStatus.NOT_PREMIUM_RECRUIT_EXCEPTION.getMessage());
        }
        return (PremiumRecruit) recruit;
    }

    // 등록 가능 공고 조회
    @Transactional(readOnly = true)
    public List<String> getAvailableRecruits(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        List<String> availableRecruits = new ArrayList<>();
        availableRecruits.add("일반 공고");

        // 프리미엄 공고 등록 가능 여부 확인
        Optional<PremiumManage> premiumManageOpt = premiumManageRepository.findByEmployerId(memberId);
        if (premiumManageOpt.isPresent()) {
            PremiumManage premiumManage = premiumManageOpt.get();

            // premiumCount가 1 이상이면 프리미엄 공고 가능
            if (premiumManage.getPremiumCount() > 0) {
                availableRecruits.add("프리미엄 공고");
            }
        }

        return availableRecruits;
    }

    // 필터를 통한 공고 전체 조회
    @Transactional(readOnly = true)
    public Page<RecruitListResponseDTO> getRecruitsWithFilters(RecruitSearchConditionDTO condition) {

        int page = (condition.getPage() != null) ? condition.getPage() : 0;
        int size = (condition.getSize() != null) ? condition.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 조회 필터
        Specification<Recruit> spec = Specification
                .where(RecruitSpecifications.isPublished())  // PUBLISHED 상태만
                .and(RecruitSpecifications.businessFieldsIn(condition.getBusinessFields()))
                .and(RecruitSpecifications.workDurationIn(condition.getWorkDurations()))
                .and(RecruitSpecifications.workDaysIn(condition.getWorkDays()))
                .and(RecruitSpecifications.workTimeIn(condition.getWorkTimes()))
                .and(RecruitSpecifications.genderEq(condition.getGender()))
                .and(RecruitSpecifications.salaryTypeEq(condition.getSalaryType()));

        Page<Recruit> recruitPage = recruitRepository.findAll(spec, pageable);
        return recruitPage.map(this::convertToRecruitListResponseDTO);
    }

    private RecruitListResponseDTO convertToRecruitListResponseDTO(Recruit recruit) {

        List<String> workTime = (recruit.getWorkTime() != null) ? new ArrayList<>(recruit.getWorkTime()) : null;
        List<String> workDays = (recruit.getWorkDays() != null) ? new ArrayList<>(recruit.getWorkDays()) : null;
        List<String> workDuration = (recruit.getWorkDuration() != null) ? new ArrayList<>(recruit.getWorkDuration()) : null;
        Set<ApplyMethod> applicationMethods = (recruit.getApplicationMethods() != null) ? new HashSet<>(recruit.getApplicationMethods()) : null;
        Set<BusinessField> businessFields = (recruit.getBusinessFields() != null) ? new HashSet<>(recruit.getBusinessFields()) : null;

        // 회사명 처리 (고용주가 Employer 타입이면 회사명을 사용)
        String companyName;
        Member employer = recruit.getEmployer();
        if (employer instanceof com.core.foreign.api.member.entity.Employer emp) {
            companyName = emp.getCompanyName();
        } else {
            companyName = employer.getName();
        }

        // 모집 기간 문자열 구성
        String recruitPeriod = null;
        if (recruit.getRecruitStartDate() != null && recruit.getRecruitEndDate() != null) {
            if("2099-12-31".equals(recruit.getRecruitEndDate().toString())){
                recruitPeriod = "상시모집";
            } else {
                recruitPeriod = recruit.getRecruitStartDate().toString()
                        + " ~ " + recruit.getRecruitEndDate().toString();
            }
        }

        return RecruitListResponseDTO.builder()
                .recruitId(recruit.getId())
                .companyName(companyName)
                .title(recruit.getTitle())
                .address(recruit.getAddress())
                .workTime(workTime)
                .workDays(workDays)
                .workDuration(workDuration)
                .salary(recruit.getSalary())
                .salaryType(recruit.getSalaryType())
                .businessFields(businessFields)
                .recruitPeriod(recruitPeriod)
                .applicationMethods(applicationMethods)
                .recruitType(recruit.getRecruitType())
                .build();
    }

    // 공고 상세 조회
    @Transactional(readOnly = true)
    public RecruitDetailResponseDTO getRecruitDetail(Long recruitId) {

        Recruit recruit = recruitRepository.findByIdFetchJoin(recruitId)
                .orElseThrow(() -> new NotFoundException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        String companyName;
        String companyIconImage;
        Address employerAddress = null;
        String employerContact = null;
        String representative = null;
        String employerEmail = null;
        String businessRegistrationNumber = null;

        Member employer = recruit.getEmployer();
        EmployerEvaluationCountDTO employerEvaluationCountDTO=evaluationReader.getEmployerEvaluation(employer.getId());
        if (employer instanceof Employer employerEntity) {
            companyName = employerEntity.getCompanyName();
            companyIconImage = employerEntity.getCompanyImageUrl();
            employerAddress = employerEntity.getAddress();
            employerContact = employerEntity.getMainPhoneNumber();
            representative = employerEntity.getName();
            employerEmail = employerEntity.getCompanyEmail();
            businessRegistrationNumber = employerEntity.getBusinessRegistrationNumber();
        } else {
            companyName = employer.getName();
            companyIconImage = null;
        }

        return RecruitDetailResponseDTO.builder()
                .recruitId(recruit.getId())
                .companyName(companyName)
                .companyIconImage(companyIconImage)
                .title(recruit.getTitle())
                .address(recruit.getAddress())
                .recruitStartDate(recruit.getRecruitStartDate())
                .recruitEndDate(recruit.getRecruitEndDate())
                .gender(recruit.getGender())
                .education(recruit.getEducation())
                .otherConditions(recruit.getOtherConditions())
                .preferredConditions(recruit.getPreferredConditions())
                .businessFields(recruit.getBusinessFields())
                .applicationMethods(recruit.getApplicationMethods())
                .workDuration(recruit.getWorkDuration())
                .workDurationOther(recruit.getWorkDurationOther())
                .workTime(recruit.getWorkTime())
                .workTimeOther(recruit.getWorkTimeOther())
                .workDays(recruit.getWorkDays())
                .workDaysOther(recruit.getWorkDaysOther())
                .salary(recruit.getSalary())
                .salaryType(recruit.getSalaryType())
                .salaryOther(recruit.getSalaryOther())
                .latitude(recruit.getLatitude())
                .longitude(recruit.getLongitude())
                .posterImageUrl(recruit.getPosterImageUrl())
                .recruitType(recruit.getRecruitType())
                .employerAddress(employerAddress)
                .employerContact(employerContact)
                .representative(representative)
                .employerEmail(employerEmail)
                .businessRegistrationNumber(businessRegistrationNumber)
                .employerEvaluationCountDTO(employerEvaluationCountDTO)
                .build();
    }

    public PageResponseDTO<MyRecruitResponseDTO> getMyRecruits(Long employerId, Integer page, Integer size, RecruitType recruitType, boolean excludeExpired){
        Pageable pageable= PageRequest.of(page, size);

        Page<MyRecruitResponseDTO> dto = recruitRepository.getMyRecruits(employerId, recruitType, RecruitPublishStatus.PUBLISHED, excludeExpired, pageable)
                .map(MyRecruitResponseDTO::from);

        PageResponseDTO<MyRecruitResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public PageResponseDTO<MyDraftRecruitResponseDTO> getMyDraftRecruits(Long employerId, Integer page, Integer size){
        Pageable pageable= PageRequest.of(page, size);

        Page<MyDraftRecruitResponseDTO> dto = recruitRepository.getMyRecruits(employerId, null , RecruitPublishStatus.DRAFT, false, pageable)
                .map(MyDraftRecruitResponseDTO::from);

        PageResponseDTO<MyDraftRecruitResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public PageResponseDTO<RecruitmentApplyStatusDTO> getRecruitmentApplyStatus(Long employerId, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Recruit> byEmployerId = recruitRepository.findPublishedRecruitsByEmployerId(employerId, pageable);
        List<Long> recruitIds=new ArrayList<>();
        List<Recruit> content = byEmployerId.getContent();
        for (Recruit recruit : content) {
            recruitIds.add(recruit.getId());
        }
        List<RecruitWithResumeCountDTO> recruitWithResumeCount = resumeRepository.findRecruitWithResumeCount(recruitIds);
        HashMap<Long, Long> resumeCount=new HashMap<>();
        for (RecruitWithResumeCountDTO recruitWithResumeCountDTO : recruitWithResumeCount) {
            resumeCount.put(recruitWithResumeCountDTO.getRecruitId(), recruitWithResumeCountDTO.getResumeCount());
        }

        Page<RecruitmentApplyStatusDTO> dto = byEmployerId.map(recruit -> RecruitmentApplyStatusDTO.from(recruit, resumeCount.get(recruit.getId())));

        PageResponseDTO<RecruitmentApplyStatusDTO> response = PageResponseDTO.of(dto);

        return response;
    }


    @Transactional
    public boolean flipRecruitBookmark(Long memberId, Long recruitId){
        Optional<RecruitBookmark> findBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(recruitId, memberId);

        if(findBookmark.isPresent()){
            RecruitBookmark recruitBookmark = findBookmark.get();
            recruitBookmarkRepository.delete(recruitBookmark);
            return false;
        }
        else{
            Member member = memberRepository.findById(memberId).get();
            Recruit recruit = recruitRepository.findById(recruitId)
                    .orElseThrow(() -> {
                        log.error("공고 없음. recruitId= {}", recruitId);
                        return new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage());
                    });

            RecruitBookmark recruitBookmark = new RecruitBookmark(recruit, member);
            recruitBookmarkRepository.save(recruitBookmark);
            return true;
        }
    }


    public PageResponseDTO<RecruitBookmarkResponseDTO> getMyRecruitBookmark(Long memberId, Integer page, Integer size){

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<RecruitBookmarkResponseDTO> dto = recruitBookmarkRepository.findByMemberId(memberId, pageable)
                .map(RecruitBookmarkResponseDTO::from);

        PageResponseDTO<RecruitBookmarkResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public List<PortfolioResponseDTO> getPortfolios(Long recruitId){

        List<PortfolioResponseDTO> response = portfolioRepository.findByRecruitId(recruitId).stream()
                .map(PortfolioResponseDTO::from).toList();

        return response;
    }

    public RecruitPreviewInContractResponseDTO getRecruitPreviewInContract(Long recruitId){
        Recruit recruit = recruitRepository.findByIdFetchJoin(recruitId)
                .orElseThrow(() -> {
                    log.error("공고 찾을 수 없음. recruitId= {}", recruitId);
                    return new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage());
                });

        return RecruitPreviewInContractResponseDTO.from(recruit);
    }

    // 프리미엄 공고 상단 점프
    @Transactional
    public void topJumpPremiumRecruit(Long recruitId, Long memberId) {
        // 프리미엄 공고 조회
        PremiumRecruit recruit = getPremiumRecruit(recruitId);

        // 해당 공고가 실제로 프리미엄 공고인지 확인
        if (!RecruitType.PREMIUM.equals(recruit.getRecruitType())) {
            throw new BadRequestException(ErrorStatus.NOT_PREMIUM_RECRUIT_EXCEPTION.getMessage());
        }

        // 요청자와 공고 등록자가 일치하는지 검증
        if (!recruit.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.ONLY_MODIFY_WRITER_USER_EXCEPTION.getMessage());
        }

        Long employerId = recruit.getEmployer().getId();
        //  프리미엄 상단 점프 횟수 차감
        int updatedRows = premiumManageRepository.decreasePremiumJumpCount(employerId);
        if (updatedRows == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_JUMP_COUNT_EXCEPTION.getMessage());
        }

        recruit.jumpNow();
        recruitRepository.save(recruit);
    }

    // 일반 공고 상단 점프
    @Transactional
    public void topJumpGeneralRecruit(Long recruitId, Long memberId) {
        // 일반 공고 조회
        GeneralRecruit recruit = getGeneralRecruit(recruitId);

        // 해당 공고가 실제로 일반 공고인지 확인
        if (!RecruitType.GENERAL.equals(recruit.getRecruitType())) {
            throw new BadRequestException(ErrorStatus.NOT_GENERAL_RECRUIT_EXCEPTION.getMessage());
        }

        // 요청자와 공고 등록자가 일치하는지 검증
        if (!recruit.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.ONLY_MODIFY_WRITER_USER_EXCEPTION.getMessage());
        }

        Long employerId = recruit.getEmployer().getId();
        // 일반 상단 점프 횟수 차감
        int updatedRows = premiumManageRepository.decreaseNormalJumpCount(employerId);
        if (updatedRows == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_GENERAL_RECRUIT_JUMP_COUNT_EXCEPTION.getMessage());
        }

        recruit.jumpNow();
        recruitRepository.save(recruit);
    }

    // 상단 점프 횟수 반환
    @Transactional(readOnly = true)
    public TopJumpCountResponseDTO getTopJumpCounts(Long memberId) {

        return premiumManageRepository.findByEmployerId(memberId)
                .map(pm -> new TopJumpCountResponseDTO(pm.getPremiumJumpCount(), pm.getNormalJumpCount()))
                .orElse(new TopJumpCountResponseDTO(0, 0));
    }

    // 상단 점프 한 공고 목록 조회
    @Transactional(readOnly = true)
    public Page<RecruitListResponseDTO> getRecruitsOrderedByJumpDate(RecruitType recruitType, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recruit> recruitPage = recruitRepository.findByRecruitTypeAndJumpDateIsNotNullOrderByJumpDateDesc(recruitType, pageable);
        return recruitPage.map(this::convertToRecruitListResponseDTO);
    }
}
