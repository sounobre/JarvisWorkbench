package com.dnobretech.jarvisworkbench.job.dto;

import com.dnobretech.jarvisworkbench.job.domain.JobType;
import jakarta.validation.constraints.NotNull;

public record CreateJobRequest(
        @NotNull(message = "Job type is required")
        JobType type,
        String metadataJson) {
}
