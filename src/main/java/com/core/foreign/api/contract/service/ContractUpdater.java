package com.core.foreign.api.contract.service;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.repository.ContractMetadataRepository;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ContractUpdater {
    private final MemberRepository memberRepository;
    private final ContractMetadataRepository contractMetadataRepository;


    public void uploadFileContract(Long memberId, Long contractMetadataId, String contractUrl){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("member not found :: memberId= {}", memberId);
                    return new BadRequestException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage());
                });


        ContractMetadata contractMetadata = contractMetadataRepository.findByContractMetadataId(contractMetadataId)
                .orElseThrow(() -> {
                    log.error("contract metadata not found: contractMetadataId= {}", contractMetadataId);
                    return new BadRequestException(ErrorStatus.CONTRACT_NOT_FOUND_EXCEPTION.getMessage());
                });


        // 이미 업로드했으면 예외
        if(member instanceof Employee){
            if(contractMetadata.getEmployeeContractStatus().equals(ContractStatus.PENDING_APPROVAL)){
                log.error("이미 업로드했음.");
                throw new BadRequestException(ErrorStatus.ALREADY_UPLOADED_EXCEPTION.getMessage());
            }

            contractMetadata.uploadEmployeeFile(contractUrl);
        }
        else if(member instanceof Employer){
            if(contractMetadata.getEmployerContractStatus().equals(ContractStatus.PENDING_APPROVAL)){
                log.error("이미 업로드했음.");
                throw new BadRequestException(ErrorStatus.ALREADY_UPLOADED_EXCEPTION.getMessage());
            }

            contractMetadata.uploadEmployerFile(contractUrl);
        }

    }
}
