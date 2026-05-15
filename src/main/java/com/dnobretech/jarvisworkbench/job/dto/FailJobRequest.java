package com.dnobretech.jarvisworkbench.job.dto;

import jakarta.validation.constraints.NotBlank;

public record FailJobRequest(
        @NotBlank String errorCode,
        @NotBlank String errorMessage
) {
}
