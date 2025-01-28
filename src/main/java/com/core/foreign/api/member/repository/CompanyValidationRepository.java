package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.CompanyValidation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyValidationRepository extends JpaRepository<CompanyValidation, Long> {

    Optional<CompanyValidation> findByBusinessNoAndStartDateAndRepresentativeName(String businessNo, String startDate, String representativeName);


    @Transactional
    @Modifying
    @Query("delete from CompanyValidation cv where cv.businessNo=:businessNo and cv.startDate=:startDate and cv.representativeName=:representativeName")
    void delete(@Param("businessNo")String businessNo,@Param("startDate")String startDate,@Param("representativeName")String representativeName);


}
