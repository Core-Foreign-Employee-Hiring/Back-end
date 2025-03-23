package com.core.foreign.api.file.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
public class UploadFile {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="upload_file_id")
    private Long id;

    private String fileUrl;
    private String originalFileName;

    public UploadFile(String fileUrl, String originalFileName) {
        this.fileUrl = fileUrl;
        this.originalFileName = originalFileName;
    }
}
