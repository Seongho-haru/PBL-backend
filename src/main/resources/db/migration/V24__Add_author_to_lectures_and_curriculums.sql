-- Add author_id to lectures table
ALTER TABLE lectures ADD COLUMN author_id BIGINT;
ALTER TABLE lectures ADD CONSTRAINT fk_lectures_author
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add author_id to curriculums table  
ALTER TABLE curriculums ADD COLUMN author_id BIGINT;
ALTER TABLE curriculums ADD CONSTRAINT fk_curriculums_author 
    FOREIGN KEY (author_id) REFERENCES users(id);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_lectures_author_id ON lectures(author_id);
CREATE INDEX IF NOT EXISTS idx_curriculums_author_id ON curriculums(author_id);

-- Update existing records to have a default author (if any users exist)
-- This is a temporary solution - in production, you'd want to handle this more carefully
UPDATE lectures SET author_id = (SELECT id FROM users LIMIT 1) WHERE author_id IS NULL;
UPDATE curriculums SET author_id = (SELECT id FROM users LIMIT 1) WHERE author_id IS NULL;

-- Make author_id NOT NULL after setting default values
ALTER TABLE lectures ALTER COLUMN author_id SET NOT NULL;
ALTER TABLE curriculums ALTER COLUMN author_id SET NOT NULL;
