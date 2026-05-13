package com.dnobretech.jarvisworkbench.job.dto;

import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;

import java.time.LocalDateTime;

public record JobResponse(
        String id,
        JobType type,
        JobStatus status,
        Integer progress,
        Integer currentStep,
        Integer totalSteps,
        String message,
        String metadataJson,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        LocalDateTime updatedAt,
        String errorCode,
        String errorMessage
) {
}
