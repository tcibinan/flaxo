UPDATE credentials SET travis_token = NULL;

DELETE FROM course_state_activated_services WHERE activated_services = 1;