-- Fixed Judge0 Schema for Spring Boot JPA compatibility
-- Create languages table with all JPA entity fields
CREATE TABLE IF NOT EXISTS languages (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    compile_cmd TEXT,
    run_cmd VARCHAR(255) NOT NULL,
    source_file VARCHAR(255) NOT NULL,
    is_archived BOOLEAN DEFAULT FALSE,
    time_limit NUMERIC(10,6) NOT NULL DEFAULT 5.000000,
    memory_limit INTEGER NOT NULL DEFAULT 128000,
    docker_image VARCHAR(255),
    docker_compile_command TEXT,
    docker_run_command TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create submissions table with all JPA entity fields
CREATE TABLE IF NOT EXISTS submissions (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(36) UNIQUE NOT NULL,
    source_code TEXT,
    language_id INTEGER NOT NULL,
    stdin TEXT,
    expected_output TEXT,
    stdout TEXT,
    stderr TEXT,
    compile_output TEXT,
    message TEXT,
    status_id INTEGER DEFAULT 1,

    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    started_at TIMESTAMP,
    queued_at TIMESTAMPTZ,
    updated_at TIMESTAMP,

    -- Execution metrics
    time DECIMAL(10,6),
    wall_time DECIMAL(10,6),
    memory INTEGER,

    -- Configuration fields
    number_of_runs INTEGER DEFAULT 1,
    cpu_time_limit DECIMAL(10,6) DEFAULT 5.0,
    cpu_extra_time DECIMAL(10,6) DEFAULT 1.0,
    wall_time_limit DECIMAL(10,6) DEFAULT 10.0,
    memory_limit INTEGER DEFAULT 128000,
    stack_limit INTEGER DEFAULT 64000,
    max_processes_and_or_threads INTEGER DEFAULT 60,
    enable_per_process_and_thread_time_limit BOOLEAN DEFAULT FALSE,
    enable_per_process_and_thread_memory_limit BOOLEAN DEFAULT FALSE,
    max_file_size INTEGER DEFAULT 1024,

    -- Optional fields
    callback_url VARCHAR(512),
    compiler_options VARCHAR(512),
    command_line_arguments VARCHAR(512),
    additional_files OID,
    redirect_stderr_to_stdout BOOLEAN DEFAULT FALSE,
    enable_network BOOLEAN DEFAULT FALSE,

    -- Host information
    execution_host TEXT,
    queue_host TEXT,

    -- Exit information
    exit_code INTEGER,
    exit_signal INTEGER,
    exit_description TEXT,
    exit_message TEXT,

    FOREIGN KEY (language_id) REFERENCES languages(id)
);

-- Create statuses table
CREATE TABLE IF NOT EXISTS statuses (
    id INTEGER PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- Create configuration table
CREATE TABLE IF NOT EXISTS configuration (
    id SERIAL PRIMARY KEY,
    key_name VARCHAR(255) NOT NULL UNIQUE,
    value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_submission_token ON submissions(token);
CREATE INDEX IF NOT EXISTS idx_submission_status ON submissions(status_id);
CREATE INDEX IF NOT EXISTS idx_submission_created_at ON submissions(created_at);
CREATE INDEX IF NOT EXISTS idx_submissions_language_id ON submissions(language_id);

-- Insert initial statuses
INSERT INTO statuses (id, description) VALUES
(1, 'In Queue'),
(2, 'Processing'),
(3, 'Accepted'),
(4, 'Wrong Answer'),
(5, 'Time Limit Exceeded'),
(6, 'Compilation Error'),
(7, 'Runtime Error (SIGSEGV)'),
(8, 'Runtime Error (SIGXFSZ)'),
(9, 'Runtime Error (SIGFPE)'),
(10, 'Runtime Error (SIGABRT)'),
(11, 'Runtime Error (NZEC)'),
(12, 'Runtime Error (Other)'),
(13, 'Internal Error'),
(14, 'Exec Format Error')
ON CONFLICT (id) DO NOTHING;
