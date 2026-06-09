package com.dnobretech.jarvisworkbench.job.application;

import com.dnobretech.jarvisworkbench.job.application.mapper.JobEventMapper;
import com.dnobretech.jarvisworkbench.job.domain.JobEvent;
import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import com.dnobretech.jarvisworkbench.job.dto.JobEventListResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobEventResponse;
import com.dnobretech.jarvisworkbench.job.dto.JobResponse;
import com.dnobretech.jarvisworkbench.job.repository.JobEventRepository;
import com.dnobretech.jarvisworkbench.job.repository.JobExecutionRepository;
import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import com.dnobretech.jarvisworkbench.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class JobEventServiceTest {

    @Mock
    private JobEventRepository jobEventRepository;

    @Mock
    private JobExecutionRepository jobRepository;

    @Mock
    private JobEventMapper jobEventMapper;

    @InjectMocks
    private JobEventService jobEventService;

    private JobExecution jobExecutionMock;
    private JobResponse jobResponseMock;
    private JobEvent jobEventMock;
    private Page<JobEvent> jobEventPageMock;
    private JobEventListResponse jobEventListResponseMock;

    @BeforeEach
    void setup()  {
        jobExecutionMock = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobEventMock = JobEvent.create(jobExecutionMock, JobEventLevel.INFO, "Iniciando", null);
        jobResponseMock = new JobResponse(jobExecutionMock.getPublicId(), JobType.EPUB_IMPORT, JobStatus.PENDING, 0, 0, 0, null,
                "{}", null, null, null, null, null, null);
        jobEventListResponseMock = new JobEventListResponse(
               Arrays.asList( new JobEventResponse(jobEventMock.getLevel(), jobEventMock.getMessage(), jobEventMock.getDetailsJson(), jobEventMock.getCreatedAt())),
                0,1,1,1);
        jobEventPageMock = new PageImpl<>(List.of(jobEventMock));
    }

    @Test
    void shouldRegisterEvent()  {
        Mockito.when(jobEventRepository.save(any(JobEvent.class))).thenReturn(jobEventMock);
        jobEventService.registerEvent(jobExecutionMock, JobEventLevel.INFO,"teste",null);
        Mockito.verify(jobEventRepository, Mockito.times(1)).save(any(JobEvent.class));

    }

    @Test
    void shouldListEvents(){
        String publicIdToFind = jobExecutionMock.getPublicId();
        Mockito.when(jobRepository.existsByPublicId(publicIdToFind)).thenReturn(true);
        Mockito.when(jobEventRepository.findByJobExecutionPublicIdOrderByCreatedAtAsc(eq(publicIdToFind), any(Pageable.class))).thenReturn(jobEventPageMock);
        Mockito.when(jobEventMapper.toListResponse(jobEventPageMock)).thenReturn(jobEventListResponseMock);

        JobEventListResponse j = jobEventService.listEvents(publicIdToFind,0,20);

        assertThat(j).isNotNull();

    }

    @Test
    void shouldThrowNotFoundWhenListingEventsFromMissingJob() {

        Mockito.when(jobRepository.existsByPublicId("123")).thenReturn(false);

        assertThatThrownBy(()-> jobEventService.listEvents("123", 0,10))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Job execution not found");
    }

    @Test
    void shouldRejectInvalidPage() {
        int invalidPage = -10;
        int size = 20;

        assertThatThrownBy(() -> jobEventService.listEvents("123", invalidPage, size))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Page must be greater than or equal to 0");

        Mockito.verifyNoInteractions(jobEventRepository);
    }

    @Test
    void shouldRejectInvalidSize() {
        int page = 0;
        int Invalidsize = 200;

        assertThatThrownBy(() -> jobEventService.listEvents("123", page, Invalidsize))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Page size must be less than or equal to 100");

        Mockito.verifyNoInteractions(jobEventRepository);
    }
}
