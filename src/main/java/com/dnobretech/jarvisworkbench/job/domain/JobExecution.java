package com.dnobretech.jarvisworkbench.job.domain;

import com.dnobretech.jarvisworkbench.job.domain.enums.JobStatus;
import com.dnobretech.jarvisworkbench.job.domain.enums.JobType;
import com.dnobretech.jarvisworkbench.shared.error.BusinessException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "job_execution")
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, length = 80, updatable = false)
    private String publicId;

    @Column(name = "progress", nullable = false)
    private Integer progress;

    @Column(name = "current_step")
    private Integer currentStep;

    @Column(name = "total_steps")
    private Integer totalSteps;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public static JobExecution create(JobType type, String metadataJson) {

        if (type == null) {
            throw new IllegalArgumentException("Job type is required");
        }

        LocalDateTime now = LocalDateTime.now();

        JobExecution jobExecution = new JobExecution();
        jobExecution.publicId = "job_" + UUID.randomUUID().toString().replace("-", "");
        jobExecution.status = JobStatus.PENDING;
        jobExecution.type = type;
        jobExecution.metadataJson = metadataJson;
        jobExecution.progress = 0;
        jobExecution.createdAt = now;
        jobExecution.updatedAt = now;
        return jobExecution;
    }

    public void start(Integer totalSteps, String message) {

        if (this.status != JobStatus.PENDING) {
            throw new BusinessException("Only pending jobs can be started");
        }

        LocalDateTime now = LocalDateTime.now();

        this.status = JobStatus.RUNNING;
        this.startedAt = now;
        this.updatedAt = now;
        this.currentStep = 0;
        this.totalSteps = totalSteps;
        this.message = message;
    }

    public void updateProgress(Integer progress, Integer currentStep, Integer totalSteps, String message) {

        if (this.status != JobStatus.RUNNING) {
            throw new BusinessException("Only running jobs can be updated");
        }

        if (Objects.isNull(progress) || progress < 0 || progress > 100) {
            throw new BusinessException("Progress must be between 0 and 100");
        }

        this.updatedAt = LocalDateTime.now();
        this.progress = progress;
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.message = message;
    }

    public void complete(String message) {

        if (this.status != JobStatus.RUNNING) {
            throw new BusinessException("Only running jobs can be completed");
        }

        LocalDateTime now = LocalDateTime.now();

        this.progress = 100;
        this.message = message;
        this.status = JobStatus.COMPLETED;
        this.finishedAt = now;
        this.updatedAt = now;
    }

    public void fail(String errorCode, String errorMessage) {

        if (this.status != JobStatus.RUNNING) {
            throw new BusinessException("Only running jobs can be failed");
        }

        if (errorCode == null || errorCode.isBlank()) {
            throw new BusinessException("Error code is required");
        }

        if (errorMessage == null || errorMessage.isBlank()) {
            throw new BusinessException("Error message is required");
        }

        LocalDateTime now = LocalDateTime.now();

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = JobStatus.FAILED;
        this.finishedAt = now;
        this.updatedAt = now;
    }

    public void cancel(String message) {

        if (this.status != JobStatus.RUNNING) {
            throw new BusinessException("Only running jobs can be cancelled");
        }

        LocalDateTime now = LocalDateTime.now();

        this.message = message;
        this.status = JobStatus.CANCELLED;
        this.finishedAt = now;
        this.updatedAt = now;
    }

    protected JobExecution() {
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public JobType getType() {
        return type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Integer getProgress() {
        return progress;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public String getMessage() {
        return message;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
