package com.core.foreign.api.contract.service;


import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractUtils {
    private final ContractMetadataRepository contractMetadataRepository;


    public void validateContractOwner(Long contractMetadataId, Long memberId){
        ContractMetadata contractMetadata = contractMetadataRepository.findByContractMetadataIdWithContract(contractMetadataId)
                .orElseThrow(() -> {
                    log.warn("[validateContractOwner][contract metadata not found][contractMetadataId= {}]", contractMetadataId);
                    return new NotFoundException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });

        Employee employee = contractMetadata.getResume().getEmployee();
        Member employer = contractMetadata.getResume().getRecruit().getEmployer();

        if(!Objects.equals(memberId, employer.getId()) && !Objects.equals(memberId, employee.getId())){
            log.warn("[uploadFileContract][계약서 소유자가 아님.][memberId= {}, contractMetadataId= {}, employer= {}, employeeId= {}]", memberId,contractMetadata.getId() , employer, employee.getId());
            throw new NotFoundException(ErrorStatus.INVALID_CONTRACT_OWNER_EXCEPTION.getMessage());
        }

    }
}
