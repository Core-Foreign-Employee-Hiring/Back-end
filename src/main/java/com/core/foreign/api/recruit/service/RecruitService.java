package com.core.foreign.api.recruit.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.dto.EmployerEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployerReliabilityDTO;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.EmployerRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.service.EvaluationReader;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.*;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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
    private final EmployerRepository employerRepository;

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

        // 필터 조건 적용 여부 체크
        boolean isFilterApplied =
                (condition.getBusinessFields() != null && !condition.getBusinessFields().isEmpty()) ||
                        (condition.getWorkDurations() != null && !condition.getWorkDurations().isEmpty()) ||
                        (condition.getWorkDays() != null && !condition.getWorkDays().isEmpty()) ||
                        (condition.getWorkTimes() != null && !condition.getWorkTimes().isEmpty()) ||
                        (condition.getGender() != null && !condition.getGender().trim().isEmpty()) ||
                        (condition.getSalaryType() != null && !condition.getSalaryType().isEmpty());

        // 페이징 객체 생성: 필터가 적용되면 createdAt 내림차순 정렬, 그렇지 않으면 정렬 없이 Specification에서 커스텀 정렬 적용
        Pageable pageable;
        if (isFilterApplied) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        } else {
            pageable = PageRequest.of(page, size);
        }

        Specification<Recruit> spec = (root, query, cb) -> {
            // 기본 조건 : PUBLISHED 상태여야 함
            Predicate predicate = cb.conjunction();
            predicate = cb.and(predicate, RecruitSpecifications.isPublished().toPredicate(root, query, cb));
            predicate = cb.and(predicate, RecruitSpecifications.businessFieldsIn(condition.getBusinessFields()).toPredicate(root, query, cb));
            predicate = cb.and(predicate, RecruitSpecifications.workDurationIn(condition.getWorkDurations()).toPredicate(root, query, cb));
            predicate = cb.and(predicate, RecruitSpecifications.workDaysIn(condition.getWorkDays()).toPredicate(root, query, cb));
            predicate = cb.and(predicate, RecruitSpecifications.workTimeIn(condition.getWorkTimes()).toPredicate(root, query, cb));
            predicate = cb.and(predicate, RecruitSpecifications.genderEq(condition.getGender()).toPredicate(root, query, cb));
            predicate = cb.and(predicate, RecruitSpecifications.salaryTypeIn(condition.getSalaryType()).toPredicate(root, query, cb));

            // 필터 조건이 없는 경우에만 커스텀 정렬 적용
            if (!isFilterApplied && query.getOrderList().isEmpty()) {
                // jumpDate가 null이면 1, null이 아니면 0을 반환하는 CASE 식
                Expression<Integer> jumpDateIsNull = cb.<Integer>selectCase()
                        .when(cb.isNull(root.get("jumpDate")), 1)
                        .otherwise(0);
                // jumpDate가 있는 경우(0)가 먼저 오고, 그 후 jumpDate 내림차순, jumpDate가 null인 경우 createdAt 내림차순
                query.orderBy(
                        cb.asc(jumpDateIsNull),
                        cb.desc(root.get("jumpDate")),
                        cb.desc(root.get("createdAt"))
                );
            }
            return predicate;
        };

        Page<Recruit> recruitPage = recruitRepository.findAll(spec, pageable);
        return recruitPage.map(this::convertToRecruitListResponseDTO);
    }

    private RecruitListResponseDTO convertToRecruitListResponseDTO(Recruit recruit) {
        List<String> workTime = (recruit.getWorkTime() != null) ? new ArrayList<>(recruit.getWorkTime()) : null;
        List<String> workDays = (recruit.getWorkDays() != null) ? new ArrayList<>(recruit.getWorkDays()) : null;
        List<String> workDuration = (recruit.getWorkDuration() != null) ? new ArrayList<>(recruit.getWorkDuration()) : null;
        Set<ApplyMethod> applicationMethods = (recruit.getApplicationMethods() != null) ? new HashSet<>(recruit.getApplicationMethods()) : null;
        Set<BusinessField> businessFields = (recruit.getBusinessFields() != null) ? new HashSet<>(recruit.getBusinessFields()) : null;

        // 고용주가 Employer 타입이면 회사명을 사용
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
            if ("2099-12-31".equals(recruit.getRecruitEndDate().toString())) {
                recruitPeriod = "상시모집";
            } else {
                recruitPeriod = recruit.getRecruitStartDate().toString() + " ~ " + recruit.getRecruitEndDate().toString();
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
                // jumpDate가 설정되어 있다면 isJump 값을 true로 반환
                .isJump(recruit.getJumpDate() != null)
                .build();
    }

    // 필터를 통한 프리미엄 공고 목록 조회
    @Transactional(readOnly = true)
    public Page<RecruitListResponseDTO> getPremiumRecruitsWithFilters(RecruitSearchConditionDTO condition) {
        int page = (condition.getPage() != null) ? condition.getPage() : 0;
        int size = (condition.getSize() != null) ? condition.getSize() : 10;

        boolean isFilterApplied =
                (condition.getBusinessFields() != null && !condition.getBusinessFields().isEmpty()) ||
                        (condition.getWorkDurations() != null && !condition.getWorkDurations().isEmpty()) ||
                        (condition.getWorkDays() != null && !condition.getWorkDays().isEmpty()) ||
                        (condition.getWorkTimes() != null && !condition.getWorkTimes().isEmpty()) ||
                        (condition.getGender() != null && !condition.getGender().trim().isEmpty()) ||
                        (condition.getSalaryType() != null && !condition.getSalaryType().isEmpty());

        Pageable pageable;
        if (!isFilterApplied) {
            pageable = PageRequest.of(page, size);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        Specification<Recruit> spec = (root, query, cb) -> {
            Predicate p = Specification.where(RecruitSpecifications.isPremium())
                    .and(RecruitSpecifications.isPublished())
                    .and(RecruitSpecifications.businessFieldsIn(condition.getBusinessFields()))
                    .and(RecruitSpecifications.workDurationIn(condition.getWorkDurations()))
                    .and(RecruitSpecifications.workDaysIn(condition.getWorkDays()))
                    .and(RecruitSpecifications.workTimeIn(condition.getWorkTimes()))
                    .and(RecruitSpecifications.genderEq(condition.getGender()))
                    .and(RecruitSpecifications.salaryTypeIn(condition.getSalaryType()))
                    .toPredicate(root, query, cb);

            // 필터 조건이 없으면 커스텀 정렬 로직 적용
            // CASE 식을 이용하여 jumpDate가 null이면 1, 그렇지 않으면 0으로 하여 오름차순 정렬하면
            // jumpDate가 있는 데이터(0)가 먼저 나오게 됨.
            if (!isFilterApplied && query.getOrderList().isEmpty()) {
                Expression<Integer> jumpDateIsNull = cb.<Integer>selectCase()
                        .when(cb.isNull(root.get("jumpDate")), 1)
                        .otherwise(0);
                query.orderBy(
                        cb.asc(jumpDateIsNull),    // jumpDate가 not null(0)이 먼저 오도록
                        cb.desc(root.get("jumpDate")), // jumpDate 내림차순 정렬
                        cb.desc(root.get("createdAt")) // jumpDate가 null인 경우 createdAt 내림차순 정렬
                );
            }
            return p;
        };

        Page<Recruit> recruitPage = recruitRepository.findAll(spec, pageable);
        return recruitPage.map(recruit -> RecruitListResponseDTO.builder()
                .recruitId(recruit.getId())
                .companyName(extractCompanyName(recruit))
                .title(recruit.getTitle())
                .address(recruit.getAddress())
                .workTime(recruit.getWorkTime() != null ? new ArrayList<>(recruit.getWorkTime()) : null)
                .workDays(recruit.getWorkDays() != null ? new ArrayList<>(recruit.getWorkDays()) : null)
                .workDuration(recruit.getWorkDuration() != null ? new ArrayList<>(recruit.getWorkDuration()) : null)
                .salary(recruit.getSalary())
                .salaryType(recruit.getSalaryType())
                .businessFields(recruit.getBusinessFields() != null ? new HashSet<>(recruit.getBusinessFields()) : null)
                .recruitPeriod(composeRecruitPeriod(recruit))
                .applicationMethods(recruit.getApplicationMethods() != null ? new HashSet<>(recruit.getApplicationMethods()) : null)
                .recruitType(recruit.getRecruitType())
                .isJump(recruit.getJumpDate() != null)
                .build());
    }

    // 회사명 추출
    private String extractCompanyName(Recruit recruit) {
        Member employer = recruit.getEmployer();
        if (employer instanceof com.core.foreign.api.member.entity.Employer emp) {
            return emp.getCompanyName();
        }
        return employer.getName();
    }

    // 모집 기간 문자열 생성
    private String composeRecruitPeriod(Recruit recruit) {
        if (recruit.getRecruitStartDate() != null && recruit.getRecruitEndDate() != null) {
            if ("2099-12-31".equals(recruit.getRecruitEndDate().toString())) {
                return "상시모집";
            } else {
                return recruit.getRecruitStartDate().toString() + " ~ " + recruit.getRecruitEndDate().toString();
            }
        }
        return null;
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

        EmployerReliabilityDTO employerReliabilityDTO = employerRepository.getEmployerReliability(employer.getId());
        Integer reliability = employerReliabilityDTO.getReliability();

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
                .employerReliability(reliability)
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

   /* public PageResponseDTO<RecruitmentApplyStatusDTO> getRecruitmentApplyStatus(Long employerId, Integer page, Integer size){
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
    }*/

    /**
     * 임시로 아래 사용 추후 공고 엔티티 수정 후 위에 코드 이용
     */

    public PageResponseDTO<RecruitmentApplyStatusDTO> getRecruitmentApplyStatus(Long employerId, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Long> publishedRecruitIdsByEmployerId = recruitRepository.findPublishedRecruitIdsByEmployerId(employerId, pageable);
        List<Long> recruitIds=publishedRecruitIdsByEmployerId.getContent();

        List<Recruit> recruits = recruitRepository.findRecruitsByIds(recruitIds);

        List<RecruitWithResumeCountDTO> recruitWithResumeCount = resumeRepository.findRecruitWithResumeCount(recruitIds);
        HashMap<Long, Long> resumeCount=new HashMap<>();
        for (RecruitWithResumeCountDTO recruitWithResumeCountDTO : recruitWithResumeCount) {
            resumeCount.put(recruitWithResumeCountDTO.getRecruitId(), recruitWithResumeCountDTO.getResumeCount());
        }

        List<RecruitmentApplyStatusDTO> content = recruits.stream().map(recruit -> RecruitmentApplyStatusDTO.from(recruit, resumeCount.get(recruit.getId()))).toList();


        PageResponseDTO<RecruitmentApplyStatusDTO> response = PageResponseDTO.<RecruitmentApplyStatusDTO>builder()
                .content(content)
                .totalPages(publishedRecruitIdsByEmployerId.getTotalPages())
                .totalElements(publishedRecruitIdsByEmployerId.getTotalElements())
                .page(page)
                .size(size)
                .build();

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

    public RecruitPreviewResponseDTO getRecruitPreview(Long recruitId){
        Recruit recruit = recruitRepository.findByIdFetchJoin(recruitId)
                .orElseThrow(() -> {
                    log.error("공고 찾을 수 없음. recruitId= {}", recruitId);
                    return new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage());
                });

        return RecruitPreviewResponseDTO.from(recruit);
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

    // 공고 검색
    @Transactional(readOnly = true)
    public PageResponseDTO<RecruitListResponseDTO> searchRecruits(String query, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recruit> recruitPage = recruitRepository.searchRecruit(query, pageable);
        Page<RecruitListResponseDTO> dtoPage = recruitPage.map(this::convertToRecruitListResponseDTO);

        return PageResponseDTO.of(dtoPage);
    }

}
