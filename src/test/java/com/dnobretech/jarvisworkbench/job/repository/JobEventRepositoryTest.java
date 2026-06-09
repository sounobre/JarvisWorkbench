package com.dnobretech.jarvisworkbench.job.repository;

import com.dnobretech.jarvisworkbench.job.domain.JobEvent;
import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobEventRepositoryTest {


    @Autowired
    private JobEventRepository jobEventRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Test
    void shouldSaveJobEvent()  {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecutionRepository.saveAndFlush(jobExecution);
        JobExecution jobExecutionSaved = jobExecutionRepository
                .findByPublicId(jobExecution.getPublicId())
                .orElseThrow();

        JobEvent jobEvent = JobEvent.create(jobExecutionSaved, JobEventLevel.INFO, "teste inserir", null);

        jobEventRepository.saveAndFlush(jobEvent);

        JobEvent jobEventSaved = jobEventRepository
                .findById(jobEvent.getId())
                .orElseThrow();

        assertThat(jobEventSaved.getId()).isNotNull();
        assertThat(jobEventSaved.getLevel()).isEqualTo(JobEventLevel.INFO);
        assertThat(jobEventSaved.getMessage()).isEqualTo("teste inserir");
        assertThat(jobEventSaved.getDetailsJson()).isNull();
        assertThat(jobEventSaved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindEventsByJobPublicId()  {
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecutionRepository.saveAndFlush(jobExecution);
        JobExecution jobExecutionSaved = jobExecutionRepository
                .findByPublicId(jobExecution.getPublicId())
                .orElseThrow();

        JobEvent jobEvent = JobEvent.create(jobExecutionSaved, JobEventLevel.INFO, "teste inserir", null);

        jobEventRepository.saveAndFlush(jobEvent);

        List<JobEvent> result = jobEventRepository.findByJobExecutionPublicIdOrderByCreatedAtAsc(jobExecution.getPublicId());

        assertThat(result).isNotNull();
        assertThat(result.getFirst().getId()).isNotNull();
        assertThat(result.getFirst().getJobExecution().getPublicId()).isEqualTo(jobExecution.getPublicId());

    }
}
