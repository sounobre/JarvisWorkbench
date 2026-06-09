package com.dnobretech.jarvisworkbench.job.application;

import com.dnobretech.jarvisworkbench.job.application.mapper.JobEventMapper;
import com.dnobretech.jarvisworkbench.job.domain.JobEvent;
import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import com.dnobretech.jarvisworkbench.job.dto.JobEventListResponse;
import com.dnobretech.jarvisworkbench.job.repository.JobEventRepository;
import com.dnobretech.jarvisworkbench.job.repository.JobExecutionRepository;
import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import com.dnobretech.jarvisworkbench.shared.error.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class JobEventService {

    private final JobEventMapper jobEventMapper;
    private final JobEventRepository jobEventRepository;
    private final JobExecutionRepository jobExecutionRepository;

    public JobEventService(JobEventMapper jobEventMapper, JobEventRepository jobEventRepository, JobExecutionRepository jobExecutionRepository) {
        this.jobEventMapper = jobEventMapper;
        this.jobEventRepository = jobEventRepository;
        this.jobExecutionRepository = jobExecutionRepository;
    }

    @Transactional
    public void registerEvent(
            JobExecution jobExecution,
            JobEventLevel level,
            String message,
            String detailsJson
    ) {
        JobEvent jobEvent = JobEvent.create(jobExecution, level, message, detailsJson);
        jobEventRepository.save(jobEvent);
    }

    @Transactional(readOnly = true)
    public JobEventListResponse listEvents(
            String jobPublicId,
            int page,
            int size
    ){

        validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        if (!jobExecutionRepository.existsByPublicId(jobPublicId)){
            throw new ResourceNotFoundException("Job execution not found");
        }

        return jobEventMapper.toListResponse(jobEventRepository.findByJobExecutionPublicIdOrderByCreatedAtAsc(jobPublicId, pageable));
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new BusinessException("Page must be greater than or equal to 0");
        }

        if (size < 1) {
            throw new BusinessException("Page size must be greater than or equal to 1");
        }

        if (size > 100) {
            throw new BusinessException("Page size must be less than or equal to 100");
        }
    }
}
