package com.core.foreign.api.contract.repository;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContractMetadataRepository extends JpaRepository<ContractMetadata, Long> {

    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.contractStatus=:contractStatus and employee.id=:employeeId ")
    Page<ContractMetadata> findByEmployeeId(@Param("employeeId") Long employeeId, @Param("contractStatus") ContractStatus contractStatus, Pageable pageable);

    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.contractStatus=:contractStatus and employer.id=:employerId")
    Page<ContractMetadata> findByEmployerId(@Param("employerId") Long employerId, @Param("contractStatus") ContractStatus contractStatus, Pageable pageable);

    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.contract" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.contractStatus=:contractStatus and employee.id=:employeeId")
    Page<ContractMetadata> findByEmployeeIdWithContract(@Param("employeeId") Long employeeId, @Param("contractStatus") ContractStatus contractStatus, Pageable pageable);

    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.contract" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.contractStatus=:contractStatus and employer.id=:employerId")
    Page<ContractMetadata> findByEmployerIdWithContract(@Param("employerId") Long employerId, @Param("contractStatus") ContractStatus contractStatus, Pageable pageable);


    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.contract" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.contractStatus='NOT_COMPLETED' and cm.contractType='IMAGE_UPLOAD'" +
            " order by cm.contract.updatedAt desc")
    Page<ContractMetadata> findNotCompleteFileUploadContractMetadataBy(Pageable pageable);

    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.contract" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where cm.id=:contractMetadataId and cm.contractStatus='NOT_COMPLETED' and cm.contractType='IMAGE_UPLOAD'")
    Optional<ContractMetadata> findNotCompleteFileUploadContractMetadataBy(@Param("contractMetadataId")Long contractMetadataId);



    @Query("select cm from ContractMetadata cm" +
            " join fetch cm.resume resume" +
            " join fetch resume.employee" +
            " join fetch resume.recruit recruit" +
            " join fetch recruit.employer" +
            " left join fetch cm.contract" +
            " where cm.id=:contractMetadataId")
    Optional<ContractMetadata> findByContractMetadataIdWithContract(@Param("contractMetadataId") Long contractMetadataId);

    @Query("select count(*)>0 from ContractMetadata cm" +
            " where cm.id=:contractMetadataId and cm.contractType='IMAGE_UPLOAD'")
    boolean isContractFileUploadContract(@Param("contractMetadataId")Long contractMetadataId);
}
