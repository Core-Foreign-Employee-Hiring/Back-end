package com.core.foreign.api.contract.service;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.contract.entity.FileUploadContract;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.repository.FileUploadContractRepository;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractCreator {
    private final ContractMetadataRepository contractMetadataRepository;
    private final FileUploadContractRepository fileUploadContractRepository;

    /**
     * 모집이 승인된 후 계약서 메타데이터 생성.
     */
    public void createContractMetadata(Resume resume, Recruit recruit){
        ContractMetadata build = ContractMetadata.builder()
                .contractType(ContractType.UNKNOWN)
                .contractStatus(ContractStatus.NOT_COMPLETED)
                .employerContractStatus(ContractStatus.NONE)
                .employeeContractStatus(ContractStatus.NONE)
                .resume(resume)
                .recruit(recruit)
                .build();

        contractMetadataRepository.save(build);
    }


    public FileUploadContract createFileUploadContract(){
        FileUploadContract fileUploadContract = new FileUploadContract();

        fileUploadContractRepository.save(fileUploadContract);

        return fileUploadContract;
    }
    
}
