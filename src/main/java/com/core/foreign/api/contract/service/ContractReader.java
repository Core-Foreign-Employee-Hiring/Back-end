package com.core.foreign.api.contract.service;

import com.core.foreign.api.contract.dto.AdminContractPreviewResponseDTO;
import com.core.foreign.api.contract.dto.ContractPreviewResponseDTO;
import com.core.foreign.api.contract.dto.EmployeeCompletedContractResponseDTO;
import com.core.foreign.api.contract.dto.EmployerCompletedContractResponseDTO;
import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.entity.FileUploadContract;
import com.core.foreign.api.contract.entity.FileUploadContractUrl;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.contract.repository.FileUploadContractUrlRepository;
import com.core.foreign.api.file.dto.FileUrlAndOriginalFileNameDTO;
import com.core.foreign.api.file.entity.UploadFile;
import com.core.foreign.api.file.repository.UploadFileRepository;
import com.core.foreign.api.member.entity.Role;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractReader {
    private final ContractMetadataRepository contractMetadataRepository;
    private final FileUploadContractUrlRepository uploadContractUrlRepository;
    private final UploadFileRepository uploadFileRepository;

    public PageResponseDTO<ContractPreviewResponseDTO> getNotCompletedContractMetadata(Role role, Long memberId, Integer page, Integer size) {

        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ContractMetadata> contractMetadata=null;

        if(role.equals(Role.EMPLOYEE)){
            contractMetadata=contractMetadataRepository.findByEmployeeId(memberId, ContractStatus.NOT_COMPLETED, pageable);
        }
        else if(role.equals(Role.EMPLOYER)) {
            contractMetadata=contractMetadataRepository.findByEmployerId(memberId, ContractStatus.NOT_COMPLETED, pageable);
        }

        Page<ContractPreviewResponseDTO> dto = contractMetadata.map(ContractPreviewResponseDTO::from);

        PageResponseDTO<ContractPreviewResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public PageResponseDTO<EmployeeCompletedContractResponseDTO>getCompletedContractMetadataOfEmployee(Long employeeId, Integer page, Integer size){
        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ContractMetadata> contractMetadata = contractMetadataRepository.findByEmployeeIdWithContract(employeeId, ContractStatus.COMPLETED, pageable);

        Page<EmployeeCompletedContractResponseDTO> dto = contractMetadata
                .map(EmployeeCompletedContractResponseDTO::from);

        PageResponseDTO<EmployeeCompletedContractResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public PageResponseDTO<EmployerCompletedContractResponseDTO>getCompletedContractMetadataOfEmployer(Long employerId, Integer page, Integer size){
        Pageable pageable= PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ContractMetadata> contractMetadata = contractMetadataRepository.findByEmployerIdWithContract(employerId, ContractStatus.COMPLETED, pageable);

        Page<EmployerCompletedContractResponseDTO> dto = contractMetadata
                .map(EmployerCompletedContractResponseDTO::from);

        PageResponseDTO<EmployerCompletedContractResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public List<FileUrlAndOriginalFileNameDTO>getCompletedFileUploadContract(Long contractMetadataId){
        ContractMetadata contractMetadata = contractMetadataRepository.findByContractMetadataIdWithContract(contractMetadataId)
                .orElseThrow(() -> {
                    log.warn("[getCompletedFileUploadContract][contractMetadata 조회 실패][contractMetadataId:{}]", contractMetadataId);
                    return new NotFoundException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });


        FileUploadContract contract = (FileUploadContract) contractMetadata.getContract();

        List<String> urls = uploadContractUrlRepository.findByFileUploadContractId(contract.getId())
                .stream().map(FileUploadContractUrl::getUrl).toList();


        List<UploadFile> uploadFiles = uploadFileRepository.findByFileUrls(urls);

        List<FileUrlAndOriginalFileNameDTO> response = uploadFiles.stream()
                .map(FileUrlAndOriginalFileNameDTO::of).toList();

        return response;
    }


    public PageResponseDTO<AdminContractPreviewResponseDTO> getNotCompleteFileUploadContractMetadata(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);

        Page<AdminContractPreviewResponseDTO> dto = contractMetadataRepository.findNotCompleteFileUploadContractMetadataBy(pageable)
                .map(AdminContractPreviewResponseDTO::from);

        PageResponseDTO<AdminContractPreviewResponseDTO> response = PageResponseDTO.of(dto);

        return response;
    }

    public List<FileUrlAndOriginalFileNameDTO> getNotFileUploadCompleteContractMetadata(Long contractMetadataId){
        ContractMetadata contractMetadata = contractMetadataRepository.findNotCompleteFileUploadContractMetadataBy(contractMetadataId)
                .orElseThrow(() -> {
                    log.warn("[getNotFileUploadCompleteContractMetadata][contractMetadata 없음. ][contractMetadataId:{}]", contractMetadataId);
                    return new NotFoundException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });

        FileUploadContract contract = (FileUploadContract) contractMetadata.getContract();
        contract.synchronizeAdminViewVersion();
        List<String> urls = uploadContractUrlRepository.findByFileUploadContractId(contract.getId())
                .stream().map(FileUploadContractUrl::getUrl).toList();


        List<UploadFile> uploadFiles = uploadFileRepository.findByFileUrls(urls);

        List<FileUrlAndOriginalFileNameDTO> response = uploadFiles.stream()
                .map(FileUrlAndOriginalFileNameDTO::of).toList();

        return response;
    }


}
