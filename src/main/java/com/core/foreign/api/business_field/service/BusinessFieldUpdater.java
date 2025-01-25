package com.core.foreign.api.business_field.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.BusinessFieldTarget;
import com.core.foreign.api.business_field.entity.BusinessFieldEntity;
import com.core.foreign.api.business_field.repository.BusinessFieldEntityRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class BusinessFieldUpdater {
    private final BusinessFieldEntityRepository businessFieldEntityRepository;
    private final MemberRepository memberRepository;

    /**
     *
     * @apiNote
     * 고용주의 업징종을 업데이트합니다.
     */
    public void updateBusinessFiledOfEmployer(Long employerId, List<BusinessField> newFileds) {
        List<BusinessFieldEntity> oldFields = businessFieldEntityRepository.findByTargetAndTargetId(BusinessFieldTarget.EMPLOYER, employerId);
        List<BusinessField> list = oldFields.stream().map(BusinessFieldEntity::getBusinessField).toList();

        // 새롭게 추가할 것. new - old
        List<BusinessField> toAdd = new ArrayList<>(newFileds);
        toAdd.removeAll(list);

        List<BusinessFieldEntity> toAddEntity=new ArrayList<>();

        for (BusinessField newFiled : toAdd) {

            BusinessFieldEntity build = BusinessFieldEntity.builder()
                    .targetId(employerId)
                    .target(BusinessFieldTarget.EMPLOYER)
                    .businessField(newFiled)
                    .build();
            toAddEntity.add(build);
        }

        // 삭제할 것 old-new
        List<Long> toDelete=new ArrayList<>();
        for (BusinessFieldEntity oldFiled : oldFields) {
            if(!newFileds.contains(oldFiled.getBusinessField())){
                toDelete.add(oldFiled.getId());
            }
        }


        businessFieldEntityRepository.saveAll(toAddEntity);
        if(!toDelete.isEmpty()){businessFieldEntityRepository.deleteByIds(toDelete);}

     }

}
