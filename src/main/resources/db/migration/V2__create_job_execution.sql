CREATE TABLE job_execution (
                               id BIGSERIAL PRIMARY KEY,

                               public_id VARCHAR(80) NOT NULL UNIQUE,

                               type VARCHAR(50) NOT NULL,
                               status VARCHAR(30) NOT NULL,

                               progress INTEGER NOT NULL DEFAULT 0,
                               current_step INTEGER,
                               total_steps INTEGER,

                               message VARCHAR(500),

                               metadata_json TEXT,

                               created_at TIMESTAMP NOT NULL,
                               started_at TIMESTAMP,
                               finished_at TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL,

                               error_code VARCHAR(100),
                               error_message VARCHAR(1000)
);

CREATE INDEX idx_job_execution_status
    ON job_execution (status);

CREATE INDEX idx_job_execution_type
    ON job_execution (type);

CREATE INDEX idx_job_execution_created_at
    ON job_execution (created_at);