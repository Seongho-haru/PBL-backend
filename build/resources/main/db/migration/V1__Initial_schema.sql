-- Judge0 Schema for Spring Boot JPA - 엔티티와 완전 일치
-- Create languages table matching Language entity
CREATE TABLE IF NOT EXISTS languages (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    compile_cmd TEXT,
    run_cmd VARCHAR(255),
    source_file VARCHAR(255),
    is_archived BOOLEAN DEFAULT FALSE,
    time_limit NUMERIC(10,6) DEFAULT 5.0,
    memory_limit INTEGER DEFAULT 128000,
    docker_image VARCHAR(255),
    docker_compile_command TEXT,
    docker_run_command TEXT
);

-- Create submission_input_output table matching SubmissionInputOutput entity
CREATE TABLE IF NOT EXISTS submission_input_output (
    id BIGSERIAL PRIMARY KEY,
    stdin TEXT,
    expected_output TEXT,
    stdout TEXT,
    stderr TEXT,
    compile_output TEXT,
    message TEXT
);

-- Create submission_constraints table matching SubmissionConstraints entity
CREATE TABLE IF NOT EXISTS submission_constraints (
    id BIGSERIAL PRIMARY KEY,
    number_of_runs INTEGER NOT NULL DEFAULT 1,
    cpu_time_limit NUMERIC(10,6) NOT NULL DEFAULT 5.0,
    cpu_extra_time NUMERIC(10,6) NOT NULL DEFAULT 1.0,
    wall_time_limit NUMERIC(10,6) NOT NULL DEFAULT 10.0,
    memory_limit INTEGER NOT NULL DEFAULT 128000,
    stack_limit INTEGER NOT NULL DEFAULT 64000,
    max_processes_and_or_threads INTEGER NOT NULL DEFAULT 60,
    enable_per_process_and_thread_time_limit BOOLEAN DEFAULT FALSE,
    enable_per_process_and_thread_memory_limit BOOLEAN DEFAULT FALSE,
    max_file_size INTEGER NOT NULL DEFAULT 1024,
    compiler_options VARCHAR(512),
    command_line_arguments VARCHAR(512),
    redirect_stderr_to_stdout BOOLEAN DEFAULT FALSE,
    callback_url VARCHAR(512),
    additional_files BYTEA,
    enable_network BOOLEAN DEFAULT FALSE
);

-- Create submissions table matching Submission entity
CREATE TABLE IF NOT EXISTS submissions (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(36) UNIQUE NOT NULL,
    source_code TEXT,
    language_id INTEGER NOT NULL,
    status_id INTEGER DEFAULT 1,
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    started_at TIMESTAMP,
    queued_at TIMESTAMP,
    updated_at TIMESTAMP,
    
    -- Host information
    queue_host VARCHAR(255),
    execution_host VARCHAR(255),
    
    -- Execution metrics
    time NUMERIC(10,6),
    memory INTEGER,
    wall_time NUMERIC(10,6),
    exit_code INTEGER,
    exit_signal INTEGER,
    
    -- Foreign keys to related tables
    input_output_id BIGINT NOT NULL,
    constraints_id BIGINT NOT NULL,
    
    FOREIGN KEY (language_id) REFERENCES languages(id),
    FOREIGN KEY (input_output_id) REFERENCES submission_input_output(id),
    FOREIGN KEY (constraints_id) REFERENCES submission_constraints(id)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_submission_token ON submissions(token);
CREATE INDEX IF NOT EXISTS idx_submission_status ON submissions(status_id);
CREATE INDEX IF NOT EXISTS idx_submission_created_at ON submissions(created_at);