package com.dnobretech.jarvisworkbench.job.api;

import com.dnobretech.jarvisworkbench.job.application.JobEventService;
import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import com.dnobretech.jarvisworkbench.job.dto.JobEventListResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobEventResponse;
import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import com.dnobretech.jarvisworkbench.shared.error.GlobalExceptionHandler;
import com.dnobretech.jarvisworkbench.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobEventController.class)
@Import(GlobalExceptionHandler.class)
public class JobEventControllerTest {

    @MockitoBean
    private JobEventService jobEventService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldFindJobEventByPublicId() throws Exception {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        JobEventListResponse jobEvent = new JobEventListResponse(
                List.of(new JobEventResponse(JobEventLevel.INFO, "Job created", null, LocalDateTime.now())),
                0,20,1,1);
        Mockito.when(jobEventService.listEvents(jobExecution.getPublicId(), 0,20)).thenReturn(jobEvent);
        mockMvc.perform(get("/api/jobs/"+jobExecution.getPublicId()+"/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].level").value("INFO"))
                .andExpect(jsonPath("$.items[0].message").value("Job created"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void shouldThrowResourceNotFoundWhenJobDoesNotExist() throws Exception {
        Mockito.when(jobEventService.listEvents("123",0,20)).thenThrow(new ResourceNotFoundException("Job execution not found"));

        mockMvc.perform(get("/api/jobs/123/events"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Job execution not found"));
    }

    @Test
    void shouldRejectPageSizeGreaterThan100() throws Exception {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        Mockito.when(jobEventService.listEvents(jobExecution.getPublicId(), 0, 150)).thenThrow(new BusinessException("Page size must be less than or equal to 100"));

        mockMvc.perform(get("/api/jobs/"+jobExecution.getPublicId()+"/events?size=150"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Page size must be less than or equal to 100"));
    }


}
