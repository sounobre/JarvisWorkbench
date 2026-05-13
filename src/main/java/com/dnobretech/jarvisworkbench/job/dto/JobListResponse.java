package com.dnobretech.jarvisworkbench.job.dto;

import java.util.List;

public record JobListResponse(
        List<JobResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
