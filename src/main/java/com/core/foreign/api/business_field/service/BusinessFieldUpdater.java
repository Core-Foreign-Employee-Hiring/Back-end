package com.core.foreign.api.business_field.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.entity.BusinessFieldEntity;
import com.core.foreign.api.business_field.entity.EmployerBusinessField;
import com.core.foreign.api.business_field.repository.BusinessFieldEntityRepository;
import com.core.foreign.api.business_field.repository.EmployerBusinessFieldRepository;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
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
    private final EmployerBusinessFieldRepository employerBusinessFieldRepository;

    /**
     *
     * 고용주의 업징종을 업데이트합니다.
     */
    public void updateBusinessFiledOfEmployer(Long employerId, List<BusinessField> newFileds) {
        List<EmployerBusinessField> olds = employerBusinessFieldRepository.findByEmployerId(employerId);
        List<BusinessField> list = olds.stream()
                .map((employerBusinessField -> employerBusinessField.getBusinessFieldEntity().getBusinessField())).toList();


        // 새롭게 추가할 것. new - old
        List<BusinessField> toAdd = new ArrayList<>(newFileds);
        toAdd.removeAll(list);

        List<BusinessFieldEntity> byBusinessFields = businessFieldEntityRepository.findByBusinessFields(toAdd);

        List<EmployerBusinessField> toAddEntity=new ArrayList<>();

        Employer employer = (Employer)memberRepository.findById(employerId).get();

        for (BusinessFieldEntity byBusinessField : byBusinessFields) {
            EmployerBusinessField employerBusinessField = new EmployerBusinessField(employer, byBusinessField);
            toAddEntity.add(employerBusinessField);
        }

        // 삭제할 것 old-new
        List<EmployerBusinessField> toDelete=new ArrayList<>();
        for (EmployerBusinessField old : olds) {
            if(!newFileds.contains(old.getBusinessFieldEntity().getBusinessField())){
                toDelete.add(old);
            }
        }


        employerBusinessFieldRepository.saveAll(toAddEntity);
        if(!toDelete.isEmpty()){employerBusinessFieldRepository.deleteAll(toDelete);}

     }

}
