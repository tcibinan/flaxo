CREATE TABLE credentials
(
  id           bigint PRIMARY KEY,
  codacy_token varchar(255),
  github_token varchar(255),
  password     varchar(255) NOT NULL,
  travis_token varchar(255)
);

CREATE TABLE flaxo_user
(
  id             bigint PRIMARY KEY,
  github_id      varchar(255) UNIQUE,
  nickname       varchar(255) NOT NULL UNIQUE,
  credentials_id bigint       NOT NULL UNIQUE REFERENCES credentials (id)
);

CREATE TABLE course_state
(
  id        bigint PRIMARY KEY,
  lifecycle integer NOT NULL
);

CREATE TABLE course_settings
(
  id                bigint PRIMARY KEY,
  language          varchar(255),
  testing_framework varchar(255),
  testing_language  varchar(255)
);

CREATE TABLE course
(
  id           bigint PRIMARY KEY,
  created_date timestamp without time zone NOT NULL,
  description  varchar(255),
  name         varchar(255)                NOT NULL,
  url          varchar(255)                NOT NULL,
  settings_id  bigint                      NOT NULL REFERENCES course_settings (id),
  state_id     bigint                      NOT NULL REFERENCES course_state (id),
  user_id      bigint                      NOT NULL REFERENCES flaxo_user (id)
);

CREATE TABLE task
(
  id        bigint PRIMARY KEY,
  branch    varchar(255) NOT NULL,
  deadline  timestamp without time zone,
  url       varchar(255) NOT NULL,
  course_id bigint       NOT NULL REFERENCES course (id)
);

CREATE TABLE student
(
  id        bigint PRIMARY KEY,
  nickname  varchar(255) NOT NULL,
  course_id bigint       NOT NULL REFERENCES course (id)
);

CREATE TABLE solution
(
  id         bigint PRIMARY KEY,
  approved   boolean NOT NULL,
  score      integer,
  student_id bigint  NOT NULL REFERENCES student (id),
  task_id    bigint  NOT NULL REFERENCES task (id)
);

CREATE TABLE build_report
(
  id          bigint PRIMARY KEY,
  date        timestamp without time zone NOT NULL,
  succeed     boolean                     NOT NULL,
  solution_id bigint                      NOT NULL REFERENCES solution (id)
);

CREATE TABLE commit
(
  id              bigint PRIMARY KEY,
  date            timestamp without time zone NOT NULL,
  pull_request_id integer,
  sha             varchar(255)                NOT NULL,
  solution_id     bigint                      NOT NULL REFERENCES solution (id)
);

CREATE TABLE code_style_report
(
  id          bigint PRIMARY KEY,
  date        timestamp without time zone NOT NULL,
  grade       integer                     NOT NULL,
  solution_id bigint                      NOT NULL REFERENCES solution (id)
);


CREATE TABLE course_state_activated_services
(
  course_state_id    bigint  NOT NULL REFERENCES course_state (id),
  activated_services integer NOT NULL
);

CREATE TABLE plagiarism_match
(
  id         bigint PRIMARY KEY,
  lines      integer      NOT NULL,
  percentage integer      NOT NULL,
  student1   varchar(255) NOT NULL,
  student2   varchar(255) NOT NULL,
  url        varchar(255) NOT NULL
);

CREATE TABLE plagiarism_report
(
  id      bigint PRIMARY KEY,
  date    timestamp without time zone NOT NULL,
  url     varchar(255)                NOT NULL,
  task_id bigint                      NOT NULL REFERENCES task (id)
);

CREATE TABLE plagiarism_report_matches
(
  plagiarism_report_id bigint NOT NULL REFERENCES plagiarism_report (id),
  matches_id           bigint NOT NULL REFERENCES plagiarism_match (id)
);

