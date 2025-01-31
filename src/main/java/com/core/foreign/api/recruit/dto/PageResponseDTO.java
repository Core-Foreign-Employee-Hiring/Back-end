package com.core.foreign.api.recruit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDTO<T> {

    private List<T> content;     // 실제 데이터 목록
    private int page;            // 현재 페이지 (0부터 시작)
    private int size;            // 요청한 페이지 사이즈
    private long totalElements;  // 전체 데이터 수
    private int totalPages;      // 전체 페이지 수

    public static <T> PageResponseDTO<T> of(Page<T> page) {
        return PageResponseDTO.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
