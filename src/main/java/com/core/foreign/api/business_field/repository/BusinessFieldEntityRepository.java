package com.core.foreign.api.business_field.repository;

import com.core.foreign.api.business_field.BusinessFieldTarget;
import com.core.foreign.api.business_field.entity.BusinessFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessFieldEntityRepository extends JpaRepository<BusinessFieldEntity, Long> {


    @Query("select b from BusinessFieldEntity b" +
            " where b.target=:target and b.targetId=:targetId")
    List<BusinessFieldEntity> findByTargetAndTargetId(@Param("target") BusinessFieldTarget target, @Param("targetId")Long targetId);


    @Modifying
    @Query("delete from BusinessFieldEntity b where b.id in :ids")
    void deleteByIds(@Param("ids")List<Long> ids);
}
