package com.dnobretech.jarvisworkbench.job.api;

import com.dnobretech.jarvisworkbench.job.application.JobService;
import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;
import com.dnobretech.jarvisworkbench.job.dto.*;
import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import com.dnobretech.jarvisworkbench.shared.error.GlobalExceptionHandler;
import com.dnobretech.jarvisworkbench.shared.error.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
@Import(GlobalExceptionHandler.class)
class JobControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateJob() throws Exception {
        CreateJobRequest createJobRequest = new CreateJobRequest(JobType.EPUB_IMPORT, "{}");
        JobResponse jobResponse = criarJobResponseMock("job_123", JobType.EPUB_IMPORT, JobStatus.PENDING);

        Mockito.when(jobService.createJob(any(CreateJobRequest.class))).thenReturn(jobResponse);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("job_123"))
                .andExpect(jsonPath("$.type").value("EPUB_IMPORT"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldNotCreateJob() throws Exception {
        CreateJobRequest createJobRequest = new CreateJobRequest(null, "{}");

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields[0].name").value("type"))
                .andExpect(jsonPath("$.fields[0].message").value("Job type is required"));
    }

    @Test
    void shouldFindJobByPublicId() throws Exception {
        JobResponse jobResponse = criarJobResponseMock("job_123", JobType.EPUB_IMPORT, JobStatus.PENDING);

        Mockito.when(jobService.findByPublicId("job_123")).thenReturn(jobResponse);

        mockMvc.perform(get("/api/jobs/job_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("job_123"));
    }

    @Test
    void shouldThrowResourceNotFoundWhenJobDoesNotExist() throws Exception {
        Mockito.when(jobService.findByPublicId("job_999")).thenThrow(new ResourceNotFoundException("Job execution not found"));

        mockMvc.perform(get("/api/jobs/job_999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Job execution not found"));
    }

    @Test
    void shouldListJobsWithoutFilters() throws Exception {
        JobListResponse jobListResponse = new JobListResponse(List.of(), 0, 20, 0, 0);

        Mockito.when(jobService.listJobs(null, null, 0, 20)).thenReturn(jobListResponse);

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void shouldRejectPageSizeGreaterThan100() throws Exception {
        Mockito.when(jobService.listJobs(null, null, 0, 150)).thenThrow(new BusinessException("Page size must be less than or equal to 100"));

        mockMvc.perform(get("/api/jobs?size=150"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Page size must be less than or equal to 100"));
    }

    @Test
    void shouldStartJob() throws Exception {
        StartJobRequest request = new StartJobRequest(10, "Starting EPUB import");
        JobResponse response = criarJobResponseMock("job_123", JobType.EPUB_IMPORT, JobStatus.RUNNING);

        Mockito.when(jobService.startJob(any(String.class), any(StartJobRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/jobs/job_123/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("job_123"))
                .andExpect(jsonPath("$.type").value("EPUB_IMPORT"))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void shouldUpdateJobProgress() throws Exception {
        UpdateJobProgressRequest request = new UpdateJobProgressRequest(
                35,
                3,
                10,
                "Extracting chapters"
        );

        JobResponse response = new JobResponse(
                "job_123",
                JobType.EPUB_IMPORT,
                JobStatus.RUNNING,
                35,
                3,
                10,
                "Extracting chapters",
                "{}",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                LocalDateTime.now(),
                null,
                null
        );

        Mockito.when(jobService.updateProgress(any(String.class), any(UpdateJobProgressRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/jobs/job_123/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("job_123"))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.progress").value(35))
                .andExpect(jsonPath("$.currentStep").value(3))
                .andExpect(jsonPath("$.totalSteps").value(10))
                .andExpect(jsonPath("$.message").value("Extracting chapters"));
    }

    @Test
    void shouldCompleteJob() throws Exception {
        CompleteJobRequest request = new CompleteJobRequest("Job completed successfully");

        JobResponse response = new JobResponse(
                "job_123",
                JobType.EPUB_IMPORT,
                JobStatus.COMPLETED,
                100,
                10,
                10,
                "Job completed successfully",
                "{}",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );

        Mockito.when(jobService.completeJob(any(String.class), any(CompleteJobRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/jobs/job_123/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.progress").value(100))
                .andExpect(jsonPath("$.message").value("Job completed successfully"));
    }

    @Test
    void shouldFailJob() throws Exception {
        FailJobRequest request = new FailJobRequest(
                "EPUB_PARSE_ERROR",
                "Unable to read EPUB spine"
        );

        JobResponse response = new JobResponse(
                "job_123",
                JobType.EPUB_IMPORT,
                JobStatus.FAILED,
                0,
                0,
                10,
                "",
                "{}",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "EPUB_PARSE_ERROR",
                "Unable to read EPUB spine"
        );

        Mockito.when(jobService.failJob(any(String.class), any(FailJobRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/jobs/job_123/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.errorCode").value("EPUB_PARSE_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Unable to read EPUB spine"));
    }

    @Test
    void shouldCancelJob() throws Exception {
        CancelJobRequest request = new CancelJobRequest(
                "Cancelled by user"
        );

        JobResponse response = new JobResponse(
                "job_123",
                JobType.EPUB_IMPORT,
                JobStatus.CANCELLED,
                100,
                0,
                0,
                "Cancelled by user",
                "{}",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );

        Mockito.when(jobService.cancelJob(any(String.class), any(CancelJobRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/jobs/job_123/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.message").value("Cancelled by user"));
    }

    @Test
    void shouldRejectInvalidProgress() throws Exception {
        UpdateJobProgressRequest request = new UpdateJobProgressRequest(
                101,
                3,
                10,
                "Invalid progress"
        );

        mockMvc.perform(patch("/api/jobs/job_123/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields[0].name").value("progress"));
    }

    @Test
    void shouldReturnNotFoundWhenStartingMissingJob() throws Exception {
        StartJobRequest request = new StartJobRequest(10, "Starting EPUB import");

        Mockito.when(jobService.startJob(any(String.class), any(StartJobRequest.class)))
                .thenThrow(new ResourceNotFoundException("Job execution not found"));

        mockMvc.perform(post("/api/jobs/job_999/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Job execution not found"));
    }

    private JobResponse criarJobResponseMock(String publicID, JobType jobType, JobStatus jobStatus) {
        return new JobResponse(publicID, jobType, jobStatus, 0, 0, 0, null, null,
                LocalDateTime.now(), null, null, LocalDateTime.now(), null, null);
    }
}
