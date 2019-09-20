ALTER TABLE course ADD COLUMN private bool;

UPDATE course c SET private = FALSE;

ALTER TABLE course ALTER COLUMN private SET NOT NULL;