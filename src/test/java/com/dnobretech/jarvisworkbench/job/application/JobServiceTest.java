package com.dnobretech.jarvisworkbench.job.application;

import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.JobType;
import com.dnobretech.jarvisworkbench.job.dto.*;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobExecutionRepository jobExecutionRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobService jobService;

    private JobExecution jobExecutionMock;
    private JobResponse jobResponseMock;

    @BeforeEach()
    void setUp(){
        jobExecutionMock = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobResponseMock = new JobResponse(jobExecutionMock.getPublicId(), JobType.EPUB_IMPORT, JobStatus.PENDING,0,0,0, null,
                "{}", null,null,null,null,null,null);
    }

    @Test
    void shouldCreateJob(){
        CreateJobRequest createJobRequest = new CreateJobRequest(JobType.EPUB_IMPORT, "{}");

        Mockito.when(jobExecutionRepository.save(any(JobExecution.class))).thenReturn(jobExecutionMock);
        Mockito.when(jobMapper.toResponse(jobExecutionMock)).thenReturn(jobResponseMock);

        JobResponse jobResponse = jobService.createJob(createJobRequest);

        assertThat(jobResponse).isNotNull();
        assertThat(jobResponse.type()).isEqualTo(JobType.EPUB_IMPORT);

        Mockito.verify(jobExecutionRepository, Mockito.times(1)).save(any(JobExecution.class));
    }

    @Test
    void shouldFindJobByPublicId(){
        String publicIdToFind = jobExecutionMock.getPublicId();
        Mockito.when(jobExecutionRepository.findByPublicId(publicIdToFind)).thenReturn(Optional.of(jobExecutionMock));
        Mockito.when(jobMapper.toResponse(jobExecutionMock)).thenReturn(jobResponseMock);

        JobResponse jobResponse = jobService.findByPublicId(publicIdToFind);

        assertThat(jobResponse).isNotNull();
        assertThat(jobResponse.id()).isEqualTo(publicIdToFind);
    }

    @Test
    void shouldThrowResourceNotFoundWhenJobDoesNotExist(){
        String fakeId = "invalid_id";
        Mockito.when(jobExecutionRepository.findByPublicId(fakeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.findByPublicId(fakeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Job execution not found");

        Mockito.verify(jobMapper, Mockito.never()).toResponse(any());
    }

    @Test
    void shouldRejectPageSizeGreaterThan100(){
        int page = 0;
        int forbiddenSize = 101;

        assertThatThrownBy(() -> jobService.listJobs(null, null, page, forbiddenSize))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Page size must be less than or equal to 100");

        Mockito.verify(jobExecutionRepository, Mockito.never()).findByStatusAndType(any(), any(), any());
    }

    @Test
    void shouldListJobsWithoutFiltersUsingFindAll() {
        int page = 0;
        int size = 20;

        Page<JobExecution> jobExecutionPage = new PageImpl<>(List.of(jobExecutionMock));
        JobListResponse jobListResponse = new JobListResponse(List.of(jobResponseMock), page, size, 1, 1);

        Mockito.when(jobExecutionRepository.findAll(any(Pageable.class))).thenReturn(jobExecutionPage);
        Mockito.when(jobMapper.toResponseList(jobExecutionPage)).thenReturn(jobListResponse);

        JobListResponse result = jobService.listJobs(null, null, page, size);

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(1);

        Mockito.verify(jobExecutionRepository).findAll(any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByStatusAndType(any(), any(), any());
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByStatus(any(), any());
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByType(any(), any());
    }

    @Test
    void shouldListJobsFilteringByStatus() {
        int page = 0;
        int size = 20;
        JobStatus status = JobStatus.PENDING;

        Page<JobExecution> jobExecutionPage = new PageImpl<>(List.of(jobExecutionMock));
        JobListResponse jobListResponse = new JobListResponse(List.of(jobResponseMock), page, size, 1, 1);

        Mockito.when(jobExecutionRepository.findByStatus(eq(status), any(Pageable.class))).thenReturn(jobExecutionPage);
        Mockito.when(jobMapper.toResponseList(jobExecutionPage)).thenReturn(jobListResponse);

        JobListResponse result = jobService.listJobs(status, null, page, size);

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(1);

        Mockito.verify(jobExecutionRepository).findByStatus(eq(status), any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findAll(any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByStatusAndType(any(), any(), any());
    }

    @Test
    void shouldListJobsFilteringByType() {
        int page = 0;
        int size = 20;
        JobType type = JobType.EPUB_IMPORT;

        Page<JobExecution> jobExecutionPage = new PageImpl<>(List.of(jobExecutionMock));
        JobListResponse jobListResponse = new JobListResponse(List.of(jobResponseMock), page, size, 1, 1);

        Mockito.when(jobExecutionRepository.findByType(eq(type), any(Pageable.class))).thenReturn(jobExecutionPage);
        Mockito.when(jobMapper.toResponseList(jobExecutionPage)).thenReturn(jobListResponse);

        JobListResponse result = jobService.listJobs(null, type, page, size);

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(1);

        Mockito.verify(jobExecutionRepository).findByType(eq(type), any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findAll(any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByStatusAndType(any(), any(), any());
    }

    @Test
    void shouldListJobsFilteringByStatusAndType() {
        int page = 0;
        int size = 20;
        JobStatus status = JobStatus.PENDING;
        JobType type = JobType.EPUB_IMPORT;

        Page<JobExecution> jobExecutionPage = new PageImpl<>(List.of(jobExecutionMock));
        JobListResponse jobListResponse = new JobListResponse(List.of(jobResponseMock), page, size, 1, 1);

        Mockito.when(jobExecutionRepository.findByStatusAndType(eq(status), eq(type), any(Pageable.class))).thenReturn(jobExecutionPage);
        Mockito.when(jobMapper.toResponseList(jobExecutionPage)).thenReturn(jobListResponse);

        JobListResponse result = jobService.listJobs(status, type, page, size);

        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isEqualTo(1);

        Mockito.verify(jobExecutionRepository).findByStatusAndType(eq(status), eq(type), any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findAll(any(Pageable.class));
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByStatus(any(), any());
        Mockito.verify(jobExecutionRepository, Mockito.never()).findByType(any(), any());
    }

    @Test
    void shouldRejectNegativePage() {
        int invalidPage = -1;
        int size = 20;

        assertThatThrownBy(() -> jobService.listJobs(null, null, invalidPage, size))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Page must be greater than or equal to 0");

        Mockito.verifyNoInteractions(jobExecutionRepository);
    }

    @Test
    void shouldRejectPageSizeLessThanOne() {
        int page = 0;
        int invalidSize = 0;

        assertThatThrownBy(() -> jobService.listJobs(null, null, page, invalidSize))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("size"); // Ajuste o texto para bater com a mensagem real do seu Service

        Mockito.verifyNoInteractions(jobExecutionRepository);
    }

    @Test
    void shouldStartJob() {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        String publicId = jobExecution.getPublicId();

        StartJobRequest request = new StartJobRequest(10, "Starting EPUB import");

        Mockito.when(jobExecutionRepository.findByPublicId(publicId))
                .thenReturn(Optional.of(jobExecution));

        mockMapperToResponseFromEntity();

        JobResponse response = jobService.startJob(publicId, request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(publicId);
        assertThat(response.status()).isEqualTo(JobStatus.RUNNING);
        assertThat(response.progress()).isEqualTo(0);
        assertThat(response.currentStep()).isEqualTo(0);
        assertThat(response.totalSteps()).isEqualTo(10);
        assertThat(response.message()).isEqualTo("Starting EPUB import");
        assertThat(response.startedAt()).isNotNull();

        Mockito.verify(jobExecutionRepository).findByPublicId(publicId);
        Mockito.verify(jobExecutionRepository).save(jobExecution);
        Mockito.verify(jobMapper).toResponse(jobExecution);
    }

    @Test
    void shouldUpdateProgress() {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecution.start(10, "Starting");

        String publicId = jobExecution.getPublicId();

        UpdateJobProgressRequest request = new UpdateJobProgressRequest(
                35,
                3,
                10,
                "Extracting chapters"
        );

        Mockito.when(jobExecutionRepository.findByPublicId(publicId))
                .thenReturn(Optional.of(jobExecution));

        mockMapperToResponseFromEntity();

        JobResponse response = jobService.updateProgress(publicId, request);

        assertThat(response.status()).isEqualTo(JobStatus.RUNNING);
        assertThat(response.progress()).isEqualTo(35);
        assertThat(response.currentStep()).isEqualTo(3);
        assertThat(response.totalSteps()).isEqualTo(10);
        assertThat(response.message()).isEqualTo("Extracting chapters");

        Mockito.verify(jobExecutionRepository).save(jobExecution);
        Mockito.verify(jobMapper).toResponse(jobExecution);
    }

    @Test
    void shouldCompleteJob() {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecution.start(10, "Starting");

        String publicId = jobExecution.getPublicId();

        CompleteJobRequest request = new CompleteJobRequest("Job completed successfully");

        Mockito.when(jobExecutionRepository.findByPublicId(publicId))
                .thenReturn(Optional.of(jobExecution));

        mockMapperToResponseFromEntity();

        JobResponse response = jobService.completeJob(publicId, request);

        assertThat(response.status()).isEqualTo(JobStatus.COMPLETED);
        assertThat(response.progress()).isEqualTo(100);
        assertThat(response.finishedAt()).isNotNull();
        assertThat(response.message()).isEqualTo("Job completed successfully");

        Mockito.verify(jobExecutionRepository).save(jobExecution);
        Mockito.verify(jobMapper).toResponse(jobExecution);
    }

    @Test
    void shouldFailJob() {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecution.start(10, "Starting");

        String publicId = jobExecution.getPublicId();

        FailJobRequest request = new FailJobRequest("EPUB_PARSE_ERROR","Unable to read EPUB spine");

        Mockito.when(jobExecutionRepository.findByPublicId(publicId))
                .thenReturn(Optional.of(jobExecution));

        mockMapperToResponseFromEntity();

        JobResponse response = jobService.failJob(publicId, request);

        assertThat(response.status()).isEqualTo(JobStatus.FAILED);
        assertThat(response.finishedAt()).isNotNull();
        assertThat(response.errorCode()).isEqualTo("EPUB_PARSE_ERROR");
        assertThat(response.errorMessage()).isEqualTo("Unable to read EPUB spine");

        Mockito.verify(jobExecutionRepository).save(jobExecution);
        Mockito.verify(jobMapper).toResponse(jobExecution);
    }

    @Test
    void shouldCancelJob() {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecution.start(10, "Starting");

        String publicId = jobExecution.getPublicId();

        CancelJobRequest request = new CancelJobRequest("Cancelled by user");

        Mockito.when(jobExecutionRepository.findByPublicId(publicId))
                .thenReturn(Optional.of(jobExecution));

        mockMapperToResponseFromEntity();

        JobResponse response = jobService.cancelJob(publicId, request);

        assertThat(response.status()).isEqualTo(JobStatus.CANCELLED);
        assertThat(response.finishedAt()).isNotNull();
        assertThat(response.message()).isEqualTo("Cancelled by user");

        Mockito.verify(jobExecutionRepository).save(jobExecution);
        Mockito.verify(jobMapper).toResponse(jobExecution);
    }

    @Test
    void shouldThrowNotFoundWhenStartingMissingJob() {
        String publicId = "job_missing";

        StartJobRequest request = new StartJobRequest(10, "Starting");

        Mockito.when(jobExecutionRepository.findByPublicId(publicId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.startJob(publicId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Job execution not found");

        Mockito.verify(jobExecutionRepository).findByPublicId(publicId);
        Mockito.verify(jobExecutionRepository, Mockito.never()).save(any());
        Mockito.verifyNoInteractions(jobMapper);
    }

    private void mockMapperToResponseFromEntity() {
        Mockito.when(jobMapper.toResponse(any(JobExecution.class)))
                .thenAnswer(invocation -> {
                    JobExecution currentJob = invocation.getArgument(0);

                    return new JobResponse(
                            currentJob.getPublicId(),
                            currentJob.getType(),
                            currentJob.getStatus(),
                            currentJob.getProgress(),
                            currentJob.getCurrentStep(),
                            currentJob.getTotalSteps(),
                            currentJob.getMessage(),
                            currentJob.getMetadataJson(),
                            currentJob.getCreatedAt(),
                            currentJob.getStartedAt(),
                            currentJob.getFinishedAt(),
                            currentJob.getUpdatedAt(),
                            currentJob.getErrorCode(),
                            currentJob.getErrorMessage()
                    );
                });
    }
}
