-- Create grade_testcase_tokens table for storing test case execution tokens
CREATE TABLE IF NOT EXISTS grade_testcase_tokens (
    id BIGSERIAL PRIMARY KEY,
    grade_id BIGINT NOT NULL,
    submission_token VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (grade_id) REFERENCES grade(id) ON DELETE CASCADE
);

-- Create index for faster grade_id lookups
CREATE INDEX IF NOT EXISTS idx_grade_testcase_tokens_grade_id ON grade_testcase_tokens(grade_id);

-- Create index for faster submission_token lookups
CREATE INDEX IF NOT EXISTS idx_grade_testcase_tokens_submission_token ON grade_testcase_tokens(submission_token);


