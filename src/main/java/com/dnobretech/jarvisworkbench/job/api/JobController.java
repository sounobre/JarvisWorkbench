package com.dnobretech.jarvisworkbench.job.api;

import com.dnobretech.jarvisworkbench.job.application.JobService;
import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;
import com.dnobretech.jarvisworkbench.job.dto.*;
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
        JobListResponse jobResponses = jobService.listJobs(status, type, page, size);
        return ResponseEntity.ok().body(jobResponses);
    }

    @PostMapping("/{publicId}/start")
    public ResponseEntity<JobResponse> startJob(@PathVariable String publicId, @Valid @RequestBody StartJobRequest startJobRequest) {

        JobResponse jobResponse = jobService.startJob(publicId, startJobRequest);

        return ResponseEntity.status(HttpStatus.OK).body(jobResponse);
    }

    @PatchMapping("/{publicId}/progress")
    public ResponseEntity<JobResponse> updateProgress(@PathVariable String publicId, @Valid @RequestBody UpdateJobProgressRequest updateJobProgressRequest) {
        JobResponse jobResponse = jobService.updateProgress(publicId, updateJobProgressRequest);

        return ResponseEntity.ok().body(jobResponse);
    }

    @PostMapping("/{publicId}/complete")
    public ResponseEntity<JobResponse> completeJob(@PathVariable String publicId, @RequestBody CompleteJobRequest completeJobRequest) {
        JobResponse jobResponse = jobService.completeJob(publicId, completeJobRequest);
        return ResponseEntity.ok().body(jobResponse);
    }

    @PostMapping("/{publicId}/fail")
    public ResponseEntity<JobResponse> failJob(@PathVariable String publicId, @Valid @RequestBody FailJobRequest failJobRequest) {
        JobResponse jobResponse = jobService.failJob(publicId, failJobRequest);
        return ResponseEntity.ok().body(jobResponse);
    }

    @PostMapping("/{publicId}/cancel")
    public ResponseEntity<JobResponse> cancelJob(@PathVariable String publicId, @RequestBody CancelJobRequest cancelJobRequest) {
        JobResponse jobResponse = jobService.cancelJob(publicId, cancelJobRequest);
        return ResponseEntity.ok().body(jobResponse);
    }
}
