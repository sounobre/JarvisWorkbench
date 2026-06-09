package com.dnobretech.jarvisworkbench.job.repository;

import com.dnobretech.jarvisworkbench.job.domain.JobExecution;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobExecutionRepositoryTest {

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Test
    void shouldSaveNewPendingJob(){
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");

        jobExecutionRepository.saveAndFlush(jobExecution);

        JobExecution jobExecutionSaved = jobExecutionRepository
                .findByPublicId(jobExecution.getPublicId())
                .orElseThrow();

        assertThat(jobExecutionSaved.getId()).isNotNull();
        assertThat(jobExecutionSaved.getPublicId()).isNotBlank();
        assertThat(jobExecutionSaved.getPublicId()).startsWith("job_");
        assertThat(jobExecutionSaved.getType()).isEqualTo(JobType.EPUB_IMPORT);
        assertThat(jobExecutionSaved.getStatus()).isEqualTo(JobStatus.PENDING);
        assertThat(jobExecutionSaved.getProgress()).isZero();
        assertThat(jobExecutionSaved.getCreatedAt()).isNotNull();
        assertThat(jobExecutionSaved.getUpdatedAt()).isNotNull();

    }

    @Test
    void shouldFindJobByPublicId(){
        JobExecution jobExecution = JobExecution.create(JobType.EPUB_IMPORT, "{}");
        jobExecutionRepository.saveAndFlush(jobExecution);

        Optional<JobExecution> result = jobExecutionRepository.findByPublicId(jobExecution.getPublicId());

        assertThat(result).isPresent();
        assertThat(result.get().getPublicId()).startsWith("job_");
        assertThat(result.get().getPublicId()).isEqualTo(jobExecution.getPublicId());

    }

    @Test
    void shouldNotCreateJobWithoutType() {
        assertThatThrownBy(() -> JobExecution.create(null, "{}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job type is required");
    }
}
