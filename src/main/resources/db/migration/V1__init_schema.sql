CREATE TABLE app_info (
                          id BIGSERIAL PRIMARY KEY,
                          app_name VARCHAR(120) NOT NULL,
                          app_version VARCHAR(50) NOT NULL,
                          description TEXT,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO app_info (
    app_name,
    app_version,
    description
) VALUES (
             'jarvis-workbench-api',
             '0.0.1-SNAPSHOT',
             'Initial database schema for Jarvis Workbench'
         );