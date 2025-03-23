package com.core.foreign.api.contract.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
public class FileUploadContractUrl {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="file_upload_contract_url")
    private Long id;

    private String url;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "file_upload_contract_id")
    private FileUploadContract fileUploadContract;

    public FileUploadContractUrl(String url, FileUploadContract fileUploadContract) {
        this.url = url;
        this.fileUploadContract = fileUploadContract;
    }
}
