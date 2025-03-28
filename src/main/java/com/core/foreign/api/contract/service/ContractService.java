package com.core.foreign.api.contract.service;

import com.core.foreign.api.contract.dto.AdminContractPreviewResponseDTO;
import com.core.foreign.api.contract.dto.ContractPreviewResponseDTO;
import com.core.foreign.api.contract.dto.EmployeeCompletedContractResponseDTO;
import com.core.foreign.api.contract.dto.EmployerCompletedContractResponseDTO;
import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.contract.entity.FileUploadContract;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.file.dto.FileUrlAndOriginalFileNameDTO;
import com.core.foreign.api.member.entity.Role;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {
    private final ContractMetadataRepository contractMetadataRepository;
    private final ContractCreator contractCreator;
    private final ContractUpdater contractUpdater;
    private final ContractUtils contractUtils;
    private final ContractReader contractReader;


    public PageResponseDTO<ContractPreviewResponseDTO> getNotCompletedContractMetadata(Role role, Long memberId, Integer page, Integer size) {
        PageResponseDTO<ContractPreviewResponseDTO> response = contractReader.getNotCompletedContractMetadata(role, memberId, page, size);

        return response;
    }

    @Transactional
    public void chooseContractType(Long employerId, Long contractMetadataId, ContractType contractType) {

        ContractMetadata contractMetadata = contractMetadataRepository.findById(contractMetadataId)
                .orElseThrow(() -> {
                    log.warn("contractMetadataId: {} not found", contractMetadataId);
                    return new NotFoundException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });

        contractUtils.validateContractOwner(contractMetadata.getId(), employerId);


        if (contractMetadata.getContractType() != ContractType.UNKNOWN) {
            log.warn("이미 계약서 형태를 선택했음. contractMetadataId= {}, 현재 형태= {}", contractMetadataId, contractMetadata.getContractType());
            throw new BadRequestException(ErrorStatus.CONTRACT_TYPE_ALREADY_SELECTED_EXCEPTION.getMessage());
        }

        contractMetadata.chooseContractType(contractType);

        // 이때 실제 계약서를 만든다.
        switch (contractType) {
            case STANDARD, AGRICULTURE ->{
                log.warn("contractType: {} not supported", contractType);
                throw new BadRequestException(ErrorStatus.NOT_READY_YET_EXCEPTION.getMessage());
            }
            case IMAGE_UPLOAD->{
                FileUploadContract fileUploadContract = contractCreator.createFileUploadContract();
                contractMetadata.setContract(fileUploadContract);
            }

        }
    }


    /**
     * 최소 등록, 수정 둘 다 이거 사용.
     */
    public void uploadFileContract(Long memberId, Long contractMetadataId, List<String> urls) {
        boolean contractFileUploadContract = contractMetadataRepository.isContractFileUploadContract(contractMetadataId);

        contractUtils.validateContractOwner(contractMetadataId, memberId);

        if (!contractFileUploadContract) {
            log.warn("파일 업로드 계약서가 아닌데 업로드 시도. contractMetadataId= {}", contractMetadataId);
            throw new BadRequestException(ErrorStatus.FILE_UPLOAD_NOT_ALLOWED_FOR_NON_FILE_CONTRACT_EXCEPTION.getMessage());

        }

        if (urls == null || urls.isEmpty()) {
            log.warn("업로드할 파일이 없습니다.");
            throw new BadRequestException(ErrorStatus.FILE_NOT_PROVIDED_EXCEPTION.getMessage());
        }


        contractUpdater.uploadFileContract(contractMetadataId, urls);

    }


    public PageResponseDTO<EmployeeCompletedContractResponseDTO>getCompletedContractMetadataOfEmployee(Long employeeId, Integer page, Integer size){
        PageResponseDTO<EmployeeCompletedContractResponseDTO> response = contractReader.getCompletedContractMetadataOfEmployee(employeeId, page, size);

        return response;
    }

    public PageResponseDTO<EmployerCompletedContractResponseDTO>getCompletedContractMetadataOfEmployer(Long employerId, Integer page, Integer size){
        PageResponseDTO<EmployerCompletedContractResponseDTO> response = contractReader.getCompletedContractMetadataOfEmployer(employerId, page, size);

        return response;
    }



    @Transactional
    public void approveOrRejectFileUploadContract(Long contractMetadataId, ContractStatus contractStatus, String rejectionReason){
        contractUpdater.approveOrRejectFileUploadContract(contractMetadataId, contractStatus, rejectionReason);
    }

    public PageResponseDTO<AdminContractPreviewResponseDTO> getNotCompleteFileUploadContractMetadata(Integer page, Integer size){
        PageResponseDTO<AdminContractPreviewResponseDTO> response = contractReader.getNotCompleteFileUploadContractMetadata(page, size);

        return response;
    }

    public List<FileUrlAndOriginalFileNameDTO> getNotFileUploadCompleteContractMetadata(Long contractMetadataId) {
        List<FileUrlAndOriginalFileNameDTO> response = contractReader.getNotFileUploadCompleteContractMetadata(contractMetadataId);

        return response;
    }

    @Transactional
    public List<FileUrlAndOriginalFileNameDTO> getCompletedFileUploadContract(Long memberId, Long contractMetadataId) {

        contractUtils.validateContractOwner(contractMetadataId, memberId);

        List<FileUrlAndOriginalFileNameDTO> response = contractReader.getCompletedFileUploadContract(contractMetadataId);

        return response;
    }
}
