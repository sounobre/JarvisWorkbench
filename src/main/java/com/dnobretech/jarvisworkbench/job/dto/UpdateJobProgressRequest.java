package com.dnobretech.jarvisworkbench.job.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateJobProgressRequest(
        @NotNull(message = "Progress is required")
        @Min(value = 0, message = "Progress must be greater than or equal to 0")
        @Max(value = 100, message = "Progress must be less than or equal to 100")
        Integer progress,

        @Min(value = 0, message = "Current step must be greater than or equal to 0")
        Integer currentStep,

        @Min(value = 0, message = "Total steps must be greater than or equal to 0")
        Integer totalSteps,

        String message
        ) {
}
