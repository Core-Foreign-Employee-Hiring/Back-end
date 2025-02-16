package com.core.foreign.api.business_field.repository;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.entity.BusinessFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessFieldEntityRepository extends JpaRepository<BusinessFieldEntity, Long> {

    @Query("select f from BusinessFieldEntity f" +
            " where f.businessField in :fields")
    List<BusinessFieldEntity> findByBusinessFields(@Param("fields") List<BusinessField> fields);


    @Modifying
    @Query("delete from BusinessFieldEntity b where b.id in :ids")
    void deleteByIds(@Param("ids")List<Long> ids);
}
