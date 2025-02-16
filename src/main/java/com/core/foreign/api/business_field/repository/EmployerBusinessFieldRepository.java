package com.core.foreign.api.business_field.repository;

import com.core.foreign.api.business_field.entity.EmployerBusinessField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployerBusinessFieldRepository extends JpaRepository<EmployerBusinessField, Long> {

    @Query("select eb from EmployerBusinessField eb" +
            " join fetch eb.businessFieldEntity")
    List<EmployerBusinessField> findByEmployerId(@Param("employerId")Long employerId);


}
