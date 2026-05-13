package com.dnobretech.jarvisworkbench.job.api;

import com.dnobretech.jarvisworkbench.job.application.JobService;
import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;
import com.dnobretech.jarvisworkbench.job.dto.CreateJobRequest;
import com.dnobretech.jarvisworkbench.job.dto.JobListResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest createJobRequest) {

        JobResponse jobResponse = jobService.createJob(createJobRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(jobResponse);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<JobResponse> getByPublicId(@PathVariable String publicId) {
        JobResponse jobResponse = jobService.findByPublicId(publicId);
        return ResponseEntity.ok(jobResponse);
    }


    @GetMapping
    public ResponseEntity<JobListResponse> getAllJobsPaginate(@RequestParam(required = false) JobStatus status,
                                                              @RequestParam(required = false) JobType type,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        JobListResponse jobResponses = jobService.listJobs( status, type, page, size);
        return ResponseEntity.ok().body(jobResponses);
    }
}
