
-- Allow all subject offering statuses used by the application.
ALTER TABLE subject_offerings
DROP CHECK subject_offerings_chk_2;

ALTER TABLE subject_offerings
ADD CONSTRAINT chk_subject_offerings_status
CHECK (status IN ('DRAFT', 'OPEN', 'FROZEN', 'RESULTS_PUBLISHED'));

-- Keep the database default consistent with the Java enum.
ALTER TABLE subject_offerings
ALTER COLUMN status SET DEFAULT 'OPEN';
