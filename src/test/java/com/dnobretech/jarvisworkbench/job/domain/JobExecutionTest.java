package com.dnobretech.jarvisworkbench.job.domain;

import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JobExecutionTest {

    @Test
    void shouldStartPendingJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");

        job.start(10, "Starting EPUB import");

        assertThat(job.getStatus()).isEqualTo(JobStatus.RUNNING);
        assertThat(job.getStartedAt()).isNotNull();
        assertThat(job.getUpdatedAt()).isNotNull();
        assertThat(job.getCurrentStep()).isEqualTo(0);
        assertThat(job.getTotalSteps()).isEqualTo(10);
        assertThat(job.getMessage()).isEqualTo("Starting EPUB import");
    }

    @Test
    void shouldNotStartCompletedJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");
        job.complete("Done");

        assertThatThrownBy(() -> job.start(10, "Starting again"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only pending jobs can be started");

        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
    }

    @Test
    void shouldUpdateProgressWhenRunning() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        job.updateProgress(35, 3, 10, "Extracting chapters");

        assertThat(job.getStatus()).isEqualTo(JobStatus.RUNNING);
        assertThat(job.getProgress()).isEqualTo(35);
        assertThat(job.getCurrentStep()).isEqualTo(3);
        assertThat(job.getTotalSteps()).isEqualTo(10);
        assertThat(job.getMessage()).isEqualTo("Extracting chapters");
        assertThat(job.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectProgressLessThanZero() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        assertThatThrownBy(() -> job.updateProgress(-1, 1, 10, "Invalid progress"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Progress must be between 0 and 100");

        assertThat(job.getProgress()).isZero();
    }

    @Test
    void shouldRejectProgressGreaterThan100() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        assertThatThrownBy(() -> job.updateProgress(101, 1, 10, "Invalid progress"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Progress must be between 0 and 100");

        assertThat(job.getProgress()).isZero();
    }

    @Test
    void shouldCompleteRunningJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        job.complete("Job completed successfully");

        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
        assertThat(job.getProgress()).isEqualTo(100);
        assertThat(job.getFinishedAt()).isNotNull();
        assertThat(job.getMessage()).isEqualTo("Job completed successfully");
    }

    @Test
    void shouldNotCompletePendingJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");

        assertThatThrownBy(() -> job.complete("Done"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only running jobs can be completed");

        assertThat(job.getStatus()).isEqualTo(JobStatus.PENDING);
        assertThat(job.getFinishedAt()).isNull();
    }

    @Test
    void shouldFailRunningJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        job.fail("EPUB_PARSE_ERROR", "Unable to read EPUB spine");

        assertThat(job.getStatus()).isEqualTo(JobStatus.FAILED);
        assertThat(job.getFinishedAt()).isNotNull();
        assertThat(job.getErrorCode()).isEqualTo("EPUB_PARSE_ERROR");
        assertThat(job.getErrorMessage()).isEqualTo("Unable to read EPUB spine");
    }

    @Test
    void shouldCancelRunningJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        job.cancel("Cancelled by user");

        assertThat(job.getStatus()).isEqualTo(JobStatus.CANCELLED);
        assertThat(job.getFinishedAt()).isNotNull();
        assertThat(job.getMessage()).isEqualTo("Cancelled by user");
    }

    @Test
    void shouldRejectFailWithoutErrorCode() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        assertThatThrownBy(() -> job.fail(null, "Invalid ErrorCode"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Error code is required");
    }

    @Test
    void shouldRejectFailWithoutErrorMessage() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        assertThatThrownBy(() -> job.fail("Invalid ErrorMessage", null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Error message is required");
    }

    @Test
    void shouldRejectNullProgress() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting");

        assertThatThrownBy(() -> job.updateProgress(null, 1, 10, "Invalid progress"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Progress must be between 0 and 100");
    }

    @Test
    void shouldNotFailPendingJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");

        assertThatThrownBy(() -> job.fail("Job is Pending", "Invalid progress"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only running jobs can be failed");
    }

    @Test
    void shouldNotCancelPendingJob() {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");

        assertThatThrownBy(() -> job.cancel("Job is Pending"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only running jobs can be cancelled");
    }


}
