package com.core.foreign.api.albareview.service;

import com.core.foreign.api.albareview.dto.AlbaReviewCommentCreateDTO;
import com.core.foreign.api.albareview.entity.AlbaReviewComment;
import com.core.foreign.api.albareview.entity.AlbaReview;
import com.core.foreign.api.albareview.repository.AlbaReviewCommentRepository;
import com.core.foreign.api.albareview.repository.AlbaReviewRepository;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.core.foreign.api.albareview.dto.AlbaReviewCommentResponseDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlbaReviewCommentService {

    private final AlbaReviewCommentRepository albaReviewCommentRepository;
    private final AlbaReviewRepository albaReviewRepository;
    private final MemberRepository memberRepository;

    // 알바 후기 댓글 등록
    @Transactional
    public void createAlbaReviewComment(AlbaReviewCommentCreateDTO albaReviewCommentCreateDTO, Long memberId) {

        // 알바 후기 조회
        AlbaReview albaReview = albaReviewRepository.findById(albaReviewCommentCreateDTO.getReviewId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ALBAREVIEW_NOT_FOUND_EXCEPTION.getMessage()));

        // 유저 정보 체크
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        AlbaReviewComment.AlbaReviewCommentBuilder commentBuilder = AlbaReviewComment.builder()
                .comment(albaReviewCommentCreateDTO.getContent())
                .albaReview(albaReview)
                .member(member);

        // parentId가 존재하면 대댓글 등록으로 처리
        if (albaReviewCommentCreateDTO.getParentId() != null) {
            // 부모 댓글 조회
            AlbaReviewComment parentComment = albaReviewCommentRepository.findById(albaReviewCommentCreateDTO.getParentId())
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.PARENT_COMMENT_NOT_FOUND_EXCEPTION.getMessage()));

            // 해당 부모 댓글에 이미 대댓글이 등록되어 있는지 확인 (한 댓글에 단 1개만 허용)
            Optional<AlbaReviewComment> existingReply =
                    albaReviewCommentRepository.findByParentComment_Id(parentComment.getId());
            if (existingReply.isPresent()) {
                throw new BadRequestException(ErrorStatus.ALREADY_ADD_CHILD_COMMENT_EXCEPTION.getMessage());
            }

            // 대댓글인 경우 부모 댓글을 설정
            commentBuilder.parentComment(parentComment);
        }

        AlbaReviewComment comment = commentBuilder.build();
        albaReviewCommentRepository.save(comment);
    }

    // 알바 후기 댓글 조회
    @Transactional(readOnly = true)
    public List<AlbaReviewCommentResponseDTO> getCommentsByReviewId(Long reviewId) {

        // 알바 후기 조회
        if (!albaReviewRepository.existsById(reviewId)) {
            throw new NotFoundException(ErrorStatus.ALBAREVIEW_NOT_FOUND_EXCEPTION.getMessage());
        }

        List<AlbaReviewComment> comments = albaReviewCommentRepository.findByAlbaReview_IdOrderByCreatedAtDesc(reviewId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return comments.stream().map(comment -> {
            AlbaReviewCommentResponseDTO albaReviewCommentResponseDTO = new AlbaReviewCommentResponseDTO();
            albaReviewCommentResponseDTO.setUserId(comment.getMember().getUserId());
            albaReviewCommentResponseDTO.setComment(comment.getComment());
            albaReviewCommentResponseDTO.setCreatedAt(comment.getCreatedAt().format(formatter));
            albaReviewCommentResponseDTO.setParentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
            return albaReviewCommentResponseDTO;
        }).collect(Collectors.toList());
    }
}
