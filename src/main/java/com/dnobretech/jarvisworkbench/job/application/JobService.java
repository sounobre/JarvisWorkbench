package com.dnobretech.jarvisworkbench.job.application;

import com.dnobretech.jarvisworkbench.job.application.mapper.JobMapper;
import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import com.dnobretech.jarvisworkbench.job.dto.*;
import com.dnobretech.jarvisworkbench.job.repository.JobExecutionRepository;
import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import com.dnobretech.jarvisworkbench.shared.error.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JobService {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobMapper jobMapper;
    private final JobEventService jobEventService;
    private final ObjectMapper objectMapper;


    public JobService(JobExecutionRepository jobExecutionRepository, JobMapper jobMapper, JobEventService jobEventService, ObjectMapper objectMapper) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobMapper = jobMapper;
        this.jobEventService = jobEventService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public JobResponse createJob(CreateJobRequest createJobRequest) {
        JobExecution jobExecution = JobExecution.create(createJobRequest.type(), createJobRequest.metadataJson());
        JobResponse response = jobMapper.toResponse(jobExecutionRepository.save(jobExecution));

        jobEventService.registerEvent(jobExecution, JobEventLevel.INFO, "Job created", null);

        return response;
    }

    @Transactional(readOnly = true)
    public JobResponse findByPublicId(String publicId) {
        JobExecution jobExecution = findByPublicIdOrThrow(publicId);
        return jobMapper.toResponse(jobExecution);
    }

    private JobExecution findByPublicIdOrThrow(String publicId) {
        Optional<JobExecution> jobExecution = jobExecutionRepository.findByPublicId(publicId);
        if (jobExecution.isPresent()) {
            return jobExecution.get();
        }
        throw new ResourceNotFoundException("Job execution not found");
    }

    @Transactional(readOnly = true)
    public JobListResponse listJobs(
            JobStatus status,
            JobType type,
            int page,
            int size
    ) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<JobExecution> jobExecutionPage;

        if (status != null && type != null) {
            jobExecutionPage = jobExecutionRepository.findByStatusAndType(status, type, pageable);
        } else if (status != null) {
            jobExecutionPage = jobExecutionRepository.findByStatus(status, pageable);
        } else if (type != null) {
            jobExecutionPage = jobExecutionRepository.findByType(type, pageable);
        } else {
            jobExecutionPage = jobExecutionRepository.findAll(pageable);
        }
        return jobMapper.toResponseList(jobExecutionPage);
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new BusinessException("Page must be greater than or equal to 0");
        }

        if (size < 1) {
            throw new BusinessException("Page size must be greater than or equal to 1");
        }

        if (size > 100) {
            throw new BusinessException("Page size must be less than or equal to 100");
        }
    }

    @Transactional
    public JobResponse startJob(String publicId, StartJobRequest request) {

        JobExecution jobExecution = findByPublicIdOrThrow(publicId);
        jobExecution.start(request.totalSteps(), request.message());
        jobExecutionRepository.save(jobExecution);

        jobEventService.registerEvent(jobExecution, JobEventLevel.INFO, "Job started", null);

        return jobMapper.toResponse(jobExecution);
    }

    @Transactional
    public JobResponse updateProgress(String publicId, UpdateJobProgressRequest request) {
        JobExecution jobExecution = findByPublicIdOrThrow(publicId);
        jobExecution.updateProgress(request.progress(), request.currentStep(), request.totalSteps(), request.message());
        jobExecutionRepository.save(jobExecution);

        jobEventService.registerEvent(jobExecution, JobEventLevel.INFO, "Job progress updated", jobProgressDetail(jobExecution));

        return jobMapper.toResponse(jobExecution);
    }

    private String jobProgressDetail(JobExecution j) {
        JobProgressDetails jobProgressDetails = new JobProgressDetails(j.getProgress(), j.getCurrentStep(), j.getTotalSteps());

        try {
            return objectMapper.writeValueAsString(jobProgressDetails);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("Unable to serialize job event details");
        }

    }

    @Transactional
    public JobResponse completeJob(String publicId, CompleteJobRequest request) {
        JobExecution jobExecution = findByPublicIdOrThrow(publicId);
        jobExecution.complete(request.message());
        jobExecutionRepository.save(jobExecution);

        jobEventService.registerEvent(jobExecution, JobEventLevel.INFO, "Job completed", null);

        return jobMapper.toResponse(jobExecution);
    }

    @Transactional
    public JobResponse failJob(String publicId, FailJobRequest request) {
        JobExecution jobExecution = findByPublicIdOrThrow(publicId);
        jobExecution.fail(request.errorCode(), request.errorMessage());
        jobExecutionRepository.save(jobExecution);

        jobEventService.registerEvent(jobExecution, JobEventLevel.ERROR, "Job failed", failJobDetail(jobExecution));

        return jobMapper.toResponse(jobExecution);
    }

    private String failJobDetail(JobExecution j) {
        FailJobRequest failJobRequest = new FailJobRequest(j.getErrorCode(), j.getErrorMessage());

        try {
            return objectMapper.writeValueAsString(failJobRequest);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("Unable to serialize job event details");
        }

    }

    @Transactional
    public JobResponse cancelJob(String publicId, CancelJobRequest request) {
        JobExecution jobExecution = findByPublicIdOrThrow(publicId);
        jobExecution.cancel(request.message());
        jobExecutionRepository.save(jobExecution);

        jobEventService.registerEvent(jobExecution, JobEventLevel.WARN, "Job cancelled", null);

        return jobMapper.toResponse(jobExecution);
    }

}
