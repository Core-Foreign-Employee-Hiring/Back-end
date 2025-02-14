package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.Evaluation;
import com.core.foreign.api.member.entity.EvaluationCategory;
import com.core.foreign.api.member.entity.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("select e from Evaluation e" +
            " where e.category=:category and e.type=:type")
    Optional<Evaluation> findByCategoryAndType(@Param("category")String category, @Param("type")EvaluationType type);

    @Query("select e from Evaluation e" +
            " where e.type=:type")
    List<Evaluation> findByType(@Param("type")EvaluationType type);

}
