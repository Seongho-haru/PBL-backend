-- Create grading_testcase_tokens table for storing test case execution tokens
CREATE TABLE IF NOT EXISTS grading_testcase_tokens (
    id BIGSERIAL PRIMARY KEY,
    grading_id BIGINT NOT NULL,
    submission_token VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (grading_id) REFERENCES grading(id) ON DELETE CASCADE
);

-- Create index for faster grading_id lookups
CREATE INDEX IF NOT EXISTS idx_grading_testcase_tokens_grading_id ON grading_testcase_tokens(grading_id);

-- Create index for faster submission_token lookups
CREATE INDEX IF NOT EXISTS idx_grading_testcase_tokens_submission_token ON grading_testcase_tokens(submission_token);
