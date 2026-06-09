package com.dnobretech.jarvisworkbench.job.application.mapper;

import com.dnobretech.jarvisworkbench.job.domain.JobEvent;
import com.dnobretech.jarvisworkbench.job.dto.JobEventListResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobEventResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobEventMapper {
    public JobEventListResponse toListResponse(Page<JobEvent> jobEvents) {
            return new JobEventListResponse(
                    toEventResponses(jobEvents.getContent().stream().toList()),
                    jobEvents.getNumber(),
                    jobEvents.getSize(),
                    jobEvents.getTotalElements(),
                    jobEvents.getTotalPages()
            );
    }

    private List<JobEventResponse> toEventResponses(List<JobEvent> lsJobevent){
        return lsJobevent.stream()
                .map(ls -> new JobEventResponse(ls.getLevel(), ls.getMessage(), ls.getDetailsJson(), ls.getCreatedAt())).toList();
    }
}
