package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.file.dto.FileUrlAndOriginalFileNameDTO;
import lombok.Getter;

import java.util.List;


@Getter
public class ResumePortfolioFileResponseDTO {
    private String title;
    private List<FileUrlAndOriginalFileNameDTO> fileUrlAndOriginalFileNameDTOS;

    public ResumePortfolioFileResponseDTO(String title, List<FileUrlAndOriginalFileNameDTO> fileUrlAndOriginalFileNameDTOS) {
        this.title = title;
        this.fileUrlAndOriginalFileNameDTOS = fileUrlAndOriginalFileNameDTOS;
    }
}
