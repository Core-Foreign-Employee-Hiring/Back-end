package com.core.foreign.api.contract.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.contract.dto.ContractPreviewResponseDTO;
import com.core.foreign.api.contract.dto.EmployeeCompletedContractResponseDTO;
import com.core.foreign.api.contract.dto.EmployerCompletedContractResponseDTO;
import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.contract.entity.FileUploadContract;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.file.FileDirAndName;
import com.core.foreign.api.member.entity.Role;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {
    private final ContractMetadataRepository contractMetadataRepository;
    private final ContractCreator contractCreator;
    private final S3Service s3Service;
    private final ContractUpdater contractUpdater;


    public Page<ContractPreviewResponseDTO> getNotCompletedContractMetadata(Role role, Long memberId, Integer page) {

        Pageable pageable= PageRequest.of(page, 4, Sort.by(Sort.Direction.DESC, "id"));
        Page<ContractMetadata> contractMetadata=null;

        if(role.equals(Role.EMPLOYEE)){
            contractMetadata=contractMetadataRepository.findByEmployeeId(memberId, ContractStatus.NOT_COMPLETED, pageable);
        }
        else if(role.equals(Role.EMPLOYER)) {
            contractMetadata=contractMetadataRepository.findByEmployerId(memberId, ContractStatus.NOT_COMPLETED, pageable);
        }

        Page<ContractPreviewResponseDTO> response = contractMetadata.map(ContractPreviewResponseDTO::from);

        return response;
    }

    @Transactional
    public void chooseContractType(Long employerId, Long contractMetadataId, ContractType contractType) {

        ContractMetadata contractMetadata = contractMetadataRepository.findById(contractMetadataId)
                .orElseThrow(() -> {
                    log.error("contractMetadataId: {} not found", contractMetadataId);
                    return new BadRequestException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });


        if(contractMetadata.getContractType()!=ContractType.UNKNOWN) {
            log.error("이미 계약서 형태를 선택했음. contractMetadataId= {}, 현재 형태= {}", contractMetadataId, contractMetadata.getContractType());
            throw  new BadRequestException(ErrorStatus.CONTRACT_TYPE_ALREADY_SELECTED_EXCEPTION.getMessage());
        }

        contractMetadata.chooseContractType(contractType);

        // 이때 실제 계약서를 만든다.
        switch (contractType) {
            case STANDARD, AGRICULTURE ->{
                log.error("contractType: {} not supported", contractType);
                throw new BadRequestException(ErrorStatus.NOT_READY_YET_EXCEPTION.getMessage());
            }
            case IMAGE_UPLOAD->{
                FileUploadContract fileUploadContract = contractCreator.createFileUploadContract();
                contractMetadata.setContract(fileUploadContract);
            }

        }
    }


    public void uploadFileContract(Long memberId, Long contractMetadataId, MultipartFile contract) {
        boolean contractFileUploadContract = contractMetadataRepository.isContractFileUploadContract(contractMetadataId);

        if (!contractFileUploadContract) {
            log.error("파일 업로드 계약서가 아닌데 업로드 시도. contractMetadataId= {}", contractMetadataId);
            throw new BadRequestException(ErrorStatus.FILE_UPLOAD_NOT_ALLOWED_FOR_NON_FILE_CONTRACT_EXCEPTION.getMessage());

        }

        if (contract == null || contract.isEmpty()) {
            log.error("업로드할 파일이 없습니다.");
            throw new BadRequestException(ErrorStatus.FILE_NOT_PROVIDED_EXCEPTION.getMessage());
        }


        try {
            // S3에 업로드.
            String url = s3Service.uploadImage(contract, FileDirAndName.FileContract);

            contractUpdater.uploadFileContract(memberId, contractMetadataId, url);
        } catch (IOException e) {
            throw new InternalServerException(ErrorStatus.FAIL_UPLOAD_EXCEPTION.getMessage());
        }

    }



    public Page<EmployeeCompletedContractResponseDTO>getCompletedContractMetadataOfEmployee(Long employeeId, Integer page){
        Pageable pageable= PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "id"));
        Page<ContractMetadata> contractMetadata = contractMetadataRepository.findByEmployeeIdWithContract(employeeId, ContractStatus.COMPLETED, pageable);

        Page<EmployeeCompletedContractResponseDTO> response = contractMetadata
                .map(EmployeeCompletedContractResponseDTO::from);

        return response;
    }

    public Page<EmployerCompletedContractResponseDTO>getCompletedContractMetadataOfEmployer(Long employerId, Integer page){
        Pageable pageable= PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "id"));
        Page<ContractMetadata> contractMetadata = contractMetadataRepository.findByEmployerIdWithContract(employerId, ContractStatus.COMPLETED, pageable);

        Page<EmployerCompletedContractResponseDTO> response = contractMetadata
                .map(EmployerCompletedContractResponseDTO::from);

        return response;
    }



    @Transactional
    public void test_approveContract(Long contractMetadataId){
        ContractMetadata contractMetadata = contractMetadataRepository.findById(contractMetadataId)
                .orElseThrow(() -> {
                    log.error("contractMetadataId: {} not found", contractMetadataId);
                    return new BadRequestException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });


        contractMetadata.completeFileUploadContract();

    }


}
