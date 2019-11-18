ALTER TABLE course_settings ADD COLUMN score_change_notification_template varchar(255);
ALTER TABLE course_settings ADD COLUMN notification_on_score_change bool;
UPDATE course_settings SET notification_on_score_change=false;
ALTER TABLE course_settings ALTER COLUMN notification_on_score_change SET NOT NULL;
