package com.dnobretech.jarvisworkbench.job.dto;

public record JobProgressDetails(Integer progress,
                                 Integer currentStep,
                                 Integer totalSteps) {
}
