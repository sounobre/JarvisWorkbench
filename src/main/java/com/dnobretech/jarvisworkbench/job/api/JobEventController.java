package com.dnobretech.jarvisworkbench.job.api;

import com.dnobretech.jarvisworkbench.job.application.JobEventService;
import com.dnobretech.jarvisworkbench.job.dto.JobEventListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobEventController {

    private final JobEventService jobEventService;

    public JobEventController(JobEventService jobEventService) {
        this.jobEventService = jobEventService;
    }

    @GetMapping("/{publicId}/events")
    public ResponseEntity<JobEventListResponse> listEventsByJobPublicId(@PathVariable String publicId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok().body(jobEventService.listEvents(publicId, page, size));
    }
}
