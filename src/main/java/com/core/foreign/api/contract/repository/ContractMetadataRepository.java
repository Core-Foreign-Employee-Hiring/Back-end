package com.core.foreign.api.contract.repository;

import com.core.foreign.api.contract.entity.ContractMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractMetadataRepository extends JpaRepository<ContractMetadata, Long> {

    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch cm.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.contractStatus='NOT_COMPLETED' and employee.id=:memberId or employer.id=:memberId")
    List<ContractMetadata> findByMemberId(@Param("memberId") Long memberId);



    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.contract" +
            " where cm.id=:contractMetadataId")
    Optional<ContractMetadata> findByContractMetadataId(@Param("contractMetadataId") Long contractMetadataId);

    @Query("select count(*)>0 from ContractMetadata cm" +
            " where cm.id=:contractMetadataId and cm.contractType='IMAGE_UPLOAD'")
    boolean isContractFilUploadContract(@Param("contractMetadataId")Long contractMetadataId);
}
