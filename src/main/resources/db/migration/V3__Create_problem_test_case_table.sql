-- Create grading table for storing grading submissions
CREATE TABLE IF NOT EXISTS grading (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(36) UNIQUE NOT NULL,
    source_code TEXT,
    language_id INTEGER NOT NULL,
    problem_id BIGINT NOT NULL,
    status_id INTEGER DEFAULT 1,
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    started_at TIMESTAMP,
    queued_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Host information
    queue_host VARCHAR(255),
    execution_host VARCHAR(255),
    
    -- Execution metrics
    time NUMERIC(10,6),
    memory INTEGER,
    wall_time NUMERIC(10,6),
    exit_code INTEGER,
    exit_signal INTEGER,
    
    -- Output fields (added for direct storage)
    message TEXT,
    error_id BIGINT,
    
    -- Foreign keys to related tables
    input_output_id BIGINT,
    constraints_id BIGINT NOT NULL,
    
    FOREIGN KEY (language_id) REFERENCES languages(id),
    FOREIGN KEY (input_output_id) REFERENCES submission_input_output(id),
    FOREIGN KEY (constraints_id) REFERENCES submission_constraints(id)
);

-- Create indexes for grading table
CREATE INDEX IF NOT EXISTS idx_grading_token ON grading(token);
CREATE INDEX IF NOT EXISTS idx_grading_status ON grading(status_id);
CREATE INDEX IF NOT EXISTS idx_grading_created_at ON grading(created_at);
CREATE INDEX IF NOT EXISTS idx_grading_problem_id ON grading(problem_id);



