package com.dnobretech.jarvisworkbench.job.api;

import com.dnobretech.jarvisworkbench.job.application.JobService;
import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;
import com.dnobretech.jarvisworkbench.job.dto.CreateJobRequest;
import com.dnobretech.jarvisworkbench.job.dto.JobListResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobResponse;
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

    private JobResponse criarJobResponseMock(String publicID, JobType jobType, JobStatus jobStatus) {
        return new JobResponse(publicID, jobType, jobStatus, 0, 0, 0, null, null,
                LocalDateTime.now(), null, null, LocalDateTime.now(), null, null);
    }
}
