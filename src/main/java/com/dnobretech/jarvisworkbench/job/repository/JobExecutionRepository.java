package com.dnobretech.jarvisworkbench.job.repository;

import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    Optional<JobExecution> findByPublicId(String publicId);

    boolean existsByPublicId(String publicId);
}
