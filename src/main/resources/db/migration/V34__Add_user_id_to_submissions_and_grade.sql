-- Add user_id column to submissions and grade tables

-- Add user_id to submissions table
ALTER TABLE submissions
ADD COLUMN user_id BIGINT;

ALTER TABLE submissions
ADD CONSTRAINT fk_submissions_user
FOREIGN KEY (user_id) REFERENCES users(id);

CREATE INDEX idx_submissions_user_id ON submissions(user_id);

-- Add user_id to grade table
ALTER TABLE grade
ADD COLUMN user_id BIGINT;

ALTER TABLE grade
ADD CONSTRAINT fk_grade_user
FOREIGN KEY (user_id) REFERENCES users(id);

CREATE INDEX idx_grade_user_id ON grade(user_id);
