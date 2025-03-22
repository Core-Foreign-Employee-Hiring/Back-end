package com.core.foreign.api.contract.repository;

import com.core.foreign.api.contract.entity.FileUploadContractUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileUploadContractUrlRepository extends JpaRepository<FileUploadContractUrl, Long> {

    @Query("select url from FileUploadContractUrl url" +
            " where url.url in :urls")
    List<FileUploadContractUrl> findByUrls(@Param("urls")List<String> urls);

    @Query("select url from FileUploadContractUrl url" +
            " where url.fileUploadContract.id=:fileUploadContractId")
    List<FileUploadContractUrl> findByFileUploadContractId(@Param("fileUploadContractId") Long fileUploadContractId);


    @Modifying
    @Query("delete from FileUploadContractUrl url where url.id in :ids")
    void deleteByIds(@Param("ids") List<Long> ids);
}
