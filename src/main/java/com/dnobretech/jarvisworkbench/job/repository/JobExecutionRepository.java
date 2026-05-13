package com.dnobretech.jarvisworkbench.job.repository;

import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    Optional<JobExecution> findByPublicId(String publicId);

    boolean existsByPublicId(String publicId);

    Page<JobExecution> findByStatus(JobStatus status, Pageable pageable);

    Page<JobExecution> findByType(JobType type, Pageable pageable);

    Page<JobExecution> findByStatusAndType(JobStatus status, JobType type, Pageable pageable);
}
