CREATE TABLE job_event (
                           id BIGSERIAL PRIMARY KEY,

                           job_execution_id BIGINT NOT NULL,

                           level VARCHAR(20) NOT NULL,
                           message VARCHAR(500) NOT NULL,
                           details_json TEXT,

                           created_at TIMESTAMP NOT NULL,

                           CONSTRAINT fk_job_event_job_execution
                               FOREIGN KEY (job_execution_id)
                                   REFERENCES job_execution(id)
                                   ON DELETE CASCADE
);

CREATE INDEX idx_job_event_job_execution_id
    ON job_event (job_execution_id);

CREATE INDEX idx_job_event_created_at
    ON job_event (created_at);

CREATE INDEX idx_job_event_level
    ON job_event (level);