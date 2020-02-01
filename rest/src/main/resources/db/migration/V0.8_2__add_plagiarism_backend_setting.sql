ALTER TABLE course_settings ADD COLUMN plagiarism_backend varchar(255);
UPDATE course_settings SET plagiarism_backend='MOSS';
ALTER TABLE course_settings
    ALTER COLUMN plagiarism_backend SET NOT NULL;
ALTER TABLE course_settings
    ADD CONSTRAINT plagiarism_backend_constraint
        CHECK (plagiarism_backend IN ('MOSS', 'JPLAG', 'COMBINED') );
