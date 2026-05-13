package com.dnobretech.jarvisworkbench.job.application;

import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.dto.JobListResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobResponse toResponse(JobExecution jobExecution) {
        return new JobResponse(
                jobExecution.getPublicId(),
                jobExecution.getType(),
                jobExecution.getStatus(),
                jobExecution.getProgress(),
                jobExecution.getCurrentStep(),
                jobExecution.getTotalSteps(),
                jobExecution.getMessage(),
                jobExecution.getMetadataJson(),
                jobExecution.getCreatedAt(),
                jobExecution.getStartedAt(),
                jobExecution.getFinishedAt(),
                jobExecution.getUpdatedAt(),
                jobExecution.getErrorCode(),
                jobExecution.getErrorMessage()
        );
    }

    public JobListResponse toResponseList(Page<JobExecution> jobExecutionPage) {
        return new JobListResponse(
                jobExecutionPage.getContent().stream().map(this::toResponse).toList(),
                jobExecutionPage.getNumber(),
                jobExecutionPage.getSize(),
                jobExecutionPage.getTotalElements(),
                jobExecutionPage.getTotalPages()
        );
    }
}
