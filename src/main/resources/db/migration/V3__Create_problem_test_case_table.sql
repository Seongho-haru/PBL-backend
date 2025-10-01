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
    stdout TEXT,
    stderr TEXT,
    compile_output TEXT,
    message TEXT,
    
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

-- Create problem_test_case table for storing test cases for problems
CREATE TABLE IF NOT EXISTS problem_test_case (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    strin TEXT,
    strout TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster problem_id lookups
CREATE INDEX IF NOT EXISTS idx_problem_test_case_problem_id ON problem_test_case(problem_id);

-- Add some sample test cases for testing
INSERT INTO problem_test_case (problem_id, strin, strout) VALUES
(1, '5\n1 2 3 4 5', '15'),
(1, '3\n10 20 30', '60'),
(1, '0', '0'),
(2, '2\n5 10', '15'),
(2, '1\n100', '100'),
-- A+B 문제 테스트케이스 (problem_id: 3)
(3, '1 2', '3'),
(3, '5 7', '12'),
(3, '10 20', '30'),
(3, '100 200', '300'),
(3, '0 0', '0'),
(3, '-5 3', '-2'),
(3, '999 1', '1000'),
(3, '1 2', '3'),
(3, '5 7', '12'),
(3, '10 20', '30')
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2'),
-- (3, '1 2', '3'),
-- (3, '5 7', '12'),
-- (3, '10 20', '30'),
-- (3, '100 200', '300'),
-- (3, '0 0', '0'),
-- (3, '-5 3', '-2')
;

