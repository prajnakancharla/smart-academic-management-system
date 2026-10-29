-- Existing offerings created before the default changed remain usable.
UPDATE subject_offerings
SET status = 'OPEN'
WHERE status = 'DRAFT';
