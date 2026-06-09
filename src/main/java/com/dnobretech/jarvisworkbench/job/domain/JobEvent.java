package com.dnobretech.jarvisworkbench.job.domain;

import com.dnobretech.jarvisworkbench.job.domain.enums.JobEventLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "job_event")
public class JobEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobEventLevel level;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "details_json", nullable = true)
    private String detailsJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_execution_id", nullable = false)
    private JobExecution jobExecution;


    public static JobEvent create(
            JobExecution jobExecution,
            JobEventLevel level,
            String message,
            String detailsJson
    ) {

        if (Objects.isNull(jobExecution)) {
            throw new IllegalArgumentException("jobExecution is required");
        }

        if (Objects.isNull(level)) {
            throw new IllegalArgumentException("level is required");
        }

        if (Objects.isNull(message) || message.isBlank()) {
            throw new IllegalArgumentException("message is required and not blank");
        }

        JobEvent jobEvent = new JobEvent();

        jobEvent.jobExecution = jobExecution;
        jobEvent.level = level;
        jobEvent.message = message;
        jobEvent.detailsJson = detailsJson;
        jobEvent.createdAt = LocalDateTime.now();


        return jobEvent;
    }

    protected JobEvent() {
    }

    public Long getId() {
        return id;
    }

    public JobEventLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public JobExecution getJobExecution() {
        return jobExecution;
    }

}
