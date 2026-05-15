package com.dnobretech.jarvisworkbench.job.dto;

import jakarta.validation.constraints.Min;

public record StartJobRequest(
        @Min(value = 0, message = "Total steps must be greater than or equal to 0")
        Integer totalSteps,
        String message
) {
}
