package com.core.foreign.api.contract.repository;

import com.core.foreign.api.contract.entity.FileUploadContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadContractRepository extends JpaRepository<FileUploadContract, Long> {
}
