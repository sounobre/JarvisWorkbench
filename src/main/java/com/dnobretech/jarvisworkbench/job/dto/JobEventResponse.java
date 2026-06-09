package com.dnobretech.jarvisworkbench.job.dto;

import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;

import java.time.LocalDateTime;

public record JobEventResponse(
        JobEventLevel level,
        String message,
        String detailsJson,
        LocalDateTime createdAt) {
}
