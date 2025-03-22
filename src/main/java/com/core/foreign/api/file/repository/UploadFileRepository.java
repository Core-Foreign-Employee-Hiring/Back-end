package com.core.foreign.api.file.repository;


import com.core.foreign.api.file.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    @Query("select file from UploadFile file" +
            " where file.fileUrl in :fileUrls")
    List<UploadFile> findByFileUrls(@Param("fileUrls") List<String> fileUrls);
}
