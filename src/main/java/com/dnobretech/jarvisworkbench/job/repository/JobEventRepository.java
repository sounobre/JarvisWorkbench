package com.dnobretech.jarvisworkbench.job.repository;

import com.dnobretech.jarvisworkbench.job.domain.JobEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobEventRepository extends JpaRepository<JobEvent, Long> {

    List<JobEvent> findByJobExecutionPublicIdOrderByCreatedAtAsc(String publicId);

    Page<JobEvent> findByJobExecutionPublicIdOrderByCreatedAtAsc(String publicId, Pageable pageable);
}
