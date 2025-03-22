package com.core.foreign.api.contract.service;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.entity.FileUploadContract;
import com.core.foreign.api.contract.entity.FileUploadContractUrl;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.contract.repository.FileUploadContractUrlRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ContractUpdater {
    private final ContractMetadataRepository contractMetadataRepository;
    private final FileUploadContractUrlRepository fileUploadContractUrlRepository;

    public void uploadFileContract(Long contractMetadataId, List<String> requestUrls){
        ContractMetadata contractMetadata = contractMetadataRepository.findByContractMetadataIdWithContract(contractMetadataId)
                .orElseThrow(() -> {
                    log.warn("[uploadFileContract][contract metadata not found][contractMetadataId= {}]", contractMetadataId);
                    return new BadRequestException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });


        if(contractMetadata.getContractStatus().equals(ContractStatus.COMPLETED)){
            log.warn("[uploadFileContract][완료된 계약서는 수정할 수 없음.][contractMetadataId= {}, contractStatus= {}]", contractMetadataId, contractMetadata.getContractStatus());
            throw new BadRequestException(ErrorStatus.CONTRACT_ALREADY_COMPLETED_EXCEPTION.getMessage());
        }

        FileUploadContract contract = (FileUploadContract)contractMetadata.getContract();

        List<FileUploadContractUrl> oldUrls = fileUploadContractUrlRepository.findByFileUploadContractId(contract.getId());

        // 새롭게 추가할 것. new-old
        List<FileUploadContractUrl> toAdd = new ArrayList<>();
        for (String requestUrl : requestUrls) {
            boolean isPresent=false;

            for (FileUploadContractUrl oldUrl : oldUrls) {
                if(oldUrl.getUrl().equals(requestUrl)){
                    isPresent=true;
                    break;
                }
            }

            if(!isPresent){
                FileUploadContractUrl fileUploadContractUrl = new FileUploadContractUrl(requestUrl, contract);
                toAdd.add(fileUploadContractUrl);
            }
        }

        // 삭제할 것 old-new
        List<Long> toDelete = new ArrayList<>();

        for (FileUploadContractUrl oldUrl : oldUrls) {
            boolean isPresent=false;

            for (String requestUrl : requestUrls) {
                if(oldUrl.getUrl().equals(requestUrl)){
                    isPresent=true;
                    break;
                }
            }

            if(!isPresent){
                toDelete.add(oldUrl.getId());
            }
        }

        fileUploadContractUrlRepository.saveAll(toAdd);
        if(!toDelete.isEmpty()){fileUploadContractUrlRepository.deleteByIds(toDelete);}


        contractMetadata.setContractStatusPendingApproval();

    }


    public void approveOrRejectFileUploadContract(Long contractMetadataId, ContractStatus contractStatus, String rejectionReason){
        ContractMetadata contractMetadata = contractMetadataRepository.findNotCompleteFileUploadContractMetadataBy(contractMetadataId)
                .orElseThrow(() -> {
                    log.warn("[approveOrRejectFileUploadContract][contract metadata not found][contractMetadataId= {}]", contractMetadataId);
                    return new BadRequestException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });

        FileUploadContract contract = (FileUploadContract) contractMetadata.getContract();
        Long curVersion = contract.getCurVersion();
        Long adminViewVersion = contract.getAdminViewVersion();

        if(adminViewVersion == null){
            log.warn("[approveOrRejectFileUploadContract][계약서 안 보고 승인/반려 시도.][contractId= {}]", contract.getId());
            throw new BadRequestException(ErrorStatus.CONTRACT_REVIEW_REQUIRED_EXCEPTION.getMessage());
        }

        if(!adminViewVersion.equals(curVersion)){
            log.warn("[approveOrRejectFileUploadContract][조회 ~ 승인/반려 사이 파일 변경 감지][contractId= {} adminViewVersion= {} curVersion= {}]", contract.getId(), adminViewVersion, curVersion);
            throw new BadRequestException(ErrorStatus.CONTRACT_VERSION_MISMATCH_EXCEPTION.getMessage());
        }

        if(contractStatus.equals(ContractStatus.APPROVED)){
            contractMetadata.approveFileUploadContract();
        }
        else if(contractStatus.equals(ContractStatus.REJECTED)){
            contractMetadata.rejectFileUploadContract(rejectionReason);
        }

    }
}
