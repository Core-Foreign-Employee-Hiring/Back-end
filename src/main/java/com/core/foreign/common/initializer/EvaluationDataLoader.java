package com.core.foreign.common.initializer;

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
public class EvaluationDataLoader implements ApplicationRunner {

    private final EvaluationRepository evaluationRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (evaluationRepository.count() == 0) { // 기존 데이터가 없는 경우만 삽입
            List<Evaluation> evaluations = Arrays.stream(EvaluationCategory.values())
                    .map(category -> new Evaluation(category.getDescription(), category.getType()))
                    .collect(Collectors.toList());

            evaluationRepository.saveAll(evaluations);
        }
    }
}

