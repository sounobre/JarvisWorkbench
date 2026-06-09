package com.dnobretech.jarvisworkbench.job.domain;

import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import com.dnobretech.jarvisworkbench.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JobEventTest {

    @Test
    void shouldCreateJobEvent()  {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting EPUB import");

        JobEvent jobEvent = JobEvent.create(job, JobEventLevel.INFO, job.getMessage(),null);

        assertThat(jobEvent.getJobExecution()).isEqualTo(job);
        assertThat(jobEvent.getLevel()).isEqualTo(JobEventLevel.INFO);
        assertThat(jobEvent.getMessage()).isEqualTo(job.getMessage());
        assertThat(jobEvent.getDetailsJson()).isNull();
    }

    @Test
    void shouldRejectEventWithoutJobExecution()  {
         assertThatThrownBy(() -> JobEvent.create(null, JobEventLevel.INFO, "teste",null))
                 .isInstanceOf(IllegalArgumentException.class)
                         .hasMessage("jobExecution is required");

    }

    @Test
    void shouldRejectEventWithoutLevel()  {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting EPUB import");

        assertThatThrownBy(() -> JobEvent.create(job, null, "teste", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("level is required");
    }

    @Test
    void shouldRejectEventWithoutMessage()  {
        JobExecution job = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        job.start(10, "Starting EPUB import");

        assertThatThrownBy(() -> JobEvent.create(job, JobEventLevel.INFO, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message is required and not blank");
    }
}
