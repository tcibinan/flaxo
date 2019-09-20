ALTER TABLE commit RENAME COLUMN pull_request_id to pull_request_number;

UPDATE commit SET pull_request_number = 0 where commit.pull_request_number is NULL;

ALTER TABLE commit ALTER COLUMN pull_request_number SET NOT NULL;
