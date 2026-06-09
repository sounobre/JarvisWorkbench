package com.dnobretech.jarvisworkbench.job.dto;

import java.util.List;

public record JobEventListResponse(
        List<JobEventResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
