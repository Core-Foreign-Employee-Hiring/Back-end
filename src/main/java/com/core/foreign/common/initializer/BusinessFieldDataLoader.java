package com.core.foreign.common.initializer;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.entity.BusinessFieldEntity;
import com.core.foreign.api.business_field.repository.BusinessFieldEntityRepository;
import com.core.foreign.api.member.entity.Evaluation;
import com.core.foreign.api.member.entity.EvaluationCategory;
import com.core.foreign.api.member.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BusinessFieldDataLoader implements ApplicationRunner {

    private final BusinessFieldEntityRepository businessFieldEntityRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (businessFieldEntityRepository.count() == 0) { // 기존 데이터가 없는 경우만 삽입
            List<BusinessFieldEntity> businessFields = Arrays.stream(BusinessField.values())
                    .map(BusinessFieldEntity::new)
                    .collect(Collectors.toList());

            businessFieldEntityRepository.saveAll(businessFields);
        }
    }
}

