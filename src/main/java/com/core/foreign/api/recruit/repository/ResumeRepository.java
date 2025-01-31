package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
