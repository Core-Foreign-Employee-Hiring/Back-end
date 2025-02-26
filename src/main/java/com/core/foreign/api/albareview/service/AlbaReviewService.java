package com.core.foreign.api.albareview.service;

import com.core.foreign.api.albareview.dto.AlbaReviewCreateDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewDetailDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewListDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewPageResponseDTO;
import com.core.foreign.api.albareview.entity.AlbaReview;
import com.core.foreign.api.albareview.repository.AlbaReviewRepository;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbaReviewService {

    private final AlbaReviewRepository albaReviewRepository;
    private final MemberRepository memberRepository;

    // 알바 후기 생성
    @Transactional
    public void createAlbaReview(AlbaReviewCreateDTO albaReviewCreateDTO, Long memberId) {

        // 유저 정보 체크
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        AlbaReview albaReview = AlbaReview.builder()
                .title(albaReviewCreateDTO.getTitle())
                .content(albaReviewCreateDTO.getContent())
                .businessField(albaReviewCreateDTO.getBusinessFields())
                .region1(albaReviewCreateDTO.getRegion1())
                .region2(albaReviewCreateDTO.getRegion2())
                .commentCount(0L)
                .readCount(0L)
                .member(member)
                .build();

        albaReviewRepository.save(albaReview);
    }

    // 알바 후기 상세 조회
    @Transactional
    public AlbaReviewDetailDTO getAlbaReviewDetail(Long reviewId, Long MemberId) {

        // 조회수 업데이트
        albaReviewRepository.incrementReadCount(reviewId);

        // 알바 후기 조회
        AlbaReview review = albaReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ALBAREVIEW_NOT_FOUND_EXCEPTION.getMessage()));

        // 현재 로그인한 사용자가 작성한 글인지 여부 체크
        boolean isMine = review.getMember().getId().equals(MemberId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedCreatedAt = review.getCreatedAt().format(formatter);

        return AlbaReviewDetailDTO.builder()
                .id(review.getId())
                .region1(review.getRegion1())
                .region2(review.getRegion2())
                .title(review.getTitle())
                .readCount(review.getReadCount())
                .commentCount(review.getCommentCount())
                .content(review.getContent())
                .userId(review.getMember().getUserId())
                .isMine(isMine)
                .createdAt(formattedCreatedAt)
                .businessField(review.getBusinessField())
                .build();
    }

    // 알바 후기 전체 조회
    @Transactional(readOnly = true)
    public AlbaReviewPageResponseDTO getAlbaReviewList(String sortType, int page, int size) {

        // 정렬 조건: sortType이 "popular"이면 조회수 기준 내림차순, 아니면 최신순(createdAt 내림차순)
        Sort sort;
        if ("popular".equalsIgnoreCase(sortType)) {
            sort = Sort.by("readCount").descending();
        } else {
            sort = Sort.by("createdAt").descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AlbaReview> reviewPage = albaReviewRepository.findAll(pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<AlbaReviewListDTO> list = reviewPage.getContent().stream()
                .map(review -> AlbaReviewListDTO.builder()
                        .id(review.getId())
                        .region1(review.getRegion1())
                        .region2(review.getRegion2())
                        .businessField(review.getBusinessField())
                        .title(review.getTitle())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt().format(formatter))
                        .readCount(review.getReadCount())
                        .commentCount(review.getCommentCount())
                        .build())
                .collect(Collectors.toList());

        return AlbaReviewPageResponseDTO.builder()
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .content(list)
                .build();
    }

    // 알바 후기 수정
    @Transactional
    public void updateAlbaReview(Long reviewId, AlbaReviewCreateDTO albaReviewCreateDTO, Long memberId) {

        // 수정할 게시글 조회 (없으면 예외 발생)
        AlbaReview review = albaReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ALBAREVIEW_NOT_FOUND_EXCEPTION.getMessage()));

        // 현재 로그인한 사용자가 작성한 글이 아니라면 수정 권한 없음
        if (!review.getMember().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.DONT_HAVE_PERMISSION_EXCEPTION.getMessage());
        }

        AlbaReview updatedReview = review.toBuilder()
                .title(albaReviewCreateDTO.getTitle())
                .content(albaReviewCreateDTO.getContent())
                .region1(albaReviewCreateDTO.getRegion1())
                .region2(albaReviewCreateDTO.getRegion2())
                .businessField(albaReviewCreateDTO.getBusinessFields())
                .build();

        albaReviewRepository.save(updatedReview);
    }

    // 알바 후기 삭제
    @Transactional
    public void deleteAlbaReview(Long reviewId, Long memberId) {

        // 삭제할 게시글 조회 (없으면 예외 발생)
        AlbaReview review = albaReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ALBAREVIEW_NOT_FOUND_EXCEPTION.getMessage()));

        // 현재 로그인한 사용자가 작성한 글이 아니라면 삭제 권한 없음
        if (!review.getMember().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.DONT_HAVE_PERMISSION_EXCEPTION.getMessage());
        }

        albaReviewRepository.delete(review);
    }

    // 알바 후기 검색
    @Transactional(readOnly = true)
    public AlbaReviewPageResponseDTO searchAlbaReview(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AlbaReview> reviewPage = albaReviewRepository.searchByTitleOrContent(keyword, pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<AlbaReviewListDTO> list = reviewPage.getContent().stream()
                .map(review -> AlbaReviewListDTO.builder()
                        .id(review.getId())
                        .region1(review.getRegion1())
                        .region2(review.getRegion2())
                        .businessField(review.getBusinessField())
                        .title(review.getTitle())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt().format(formatter))
                        .readCount(review.getReadCount())
                        .commentCount(review.getCommentCount())
                        .build())
                .collect(Collectors.toList());

        return AlbaReviewPageResponseDTO.builder()
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .content(list)
                .build();
    }
}
