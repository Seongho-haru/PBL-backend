-- Update status codes to match Judge0 standard
-- Add Memory Limit Exceeded status and adjust other status IDs

-- First, clear existing statuses
DELETE FROM statuses;

-- Insert updated statuses with correct IDs
INSERT INTO statuses (id, description) VALUES
(1, 'In Queue'),
(2, 'Processing'),
(3, 'Accepted'),
(4, 'Wrong Answer'),
(5, 'Time Limit Exceeded'),
(6, 'Memory Limit Exceeded'),
(7, 'Compilation Error'),
(8, 'Runtime Error (SIGSEGV)'),
(9, 'Runtime Error (SIGXFSZ)'),
(10, 'Runtime Error (SIGFPE)'),
(11, 'Runtime Error (SIGABRT)'),
(12, 'Runtime Error (NZEC)'),
(13, 'Runtime Error (Other)'),
(14, 'Internal Error'),
(15, 'Exec Format Error');
