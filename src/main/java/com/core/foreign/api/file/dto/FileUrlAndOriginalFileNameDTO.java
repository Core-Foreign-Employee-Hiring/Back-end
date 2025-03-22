package com.core.foreign.api.file.dto;

import com.core.foreign.api.file.entity.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUrlAndOriginalFileNameDTO {
    private String fileUrl;
    private String originalFileName;

    public static FileUrlAndOriginalFileNameDTO of(UploadFile file){
        return new FileUrlAndOriginalFileNameDTO(file.getFileUrl(), file.getOriginalFileName());
    }
}
