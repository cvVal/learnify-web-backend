CREATE TABLE IF NOT EXISTS users (
    id              SERIAL PRIMARY KEY,
    first_name      VARCHAR(15) NOT NULL,
    last_name       VARCHAR(15) NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    password        VARCHAR(50) NOT NULL,
    roles           TEXT[] NOT NULL,
    profile_picture VARCHAR(255),
    bio             TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    id              SERIAL PRIMARY KEY,
    title           VARCHAR(150) NOT NULL,
    description     TEXT NOT NULL,
    price           FLOAT,
    duration        INT,
    category        VARCHAR(30),
    tags            TEXT[],
    level           VARCHAR(15),
    instructor_id   INT NOT NULL,
    lesson_ids      TEXT[],
    pre_requisites  TEXT,
    image_url       VARCHAR(255),
    is_published    VARCHAR(5) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_instructor
    FOREIGN KEY (instructor_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_course_user_id
    ON courses (instructor_id);

CREATE TABLE IF NOT EXISTS lessons (
    id                SERIAL PRIMARY KEY,
    title             VARCHAR(100) NOT NULL,
    description       TEXT,
    duration          INT,
    status            VARCHAR(15) NOT NULL,
    course_id         INT NOT NULL,
    content_url       VARCHAR(255),
    order_number      INT NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course
    FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE INDEX IF NOT EXISTS idx_lesson_course_id
    ON lessons (course_id);

CREATE TABLE IF NOT EXISTS quizzes (
    id                SERIAL PRIMARY KEY,
    lesson_id         INT NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson
    FOREIGN KEY (lesson_id) REFERENCES lessons (id)
);

CREATE INDEX IF NOT EXISTS idx_quiz_lesson_id
    ON quizzes (lesson_id);

CREATE TABLE IF NOT EXISTS questions (
    id                SERIAL PRIMARY KEY,
    quiz_id           INT NOT NULL,
    question          TEXT NOT NULL,
    options           TEXT[],
    correct_option    VARCHAR(1),
    CONSTRAINT fk_quiz
    FOREIGN KEY (quiz_id) REFERENCES quizzes (id)
);

CREATE INDEX IF NOT EXISTS idx_question_quiz_id
    ON questions (quiz_id);

CREATE TABLE IF NOT EXISTS resources (
    id            SERIAL PRIMARY KEY,
    lesson_id     INT NOT NULL,
    name          VARCHAR(50) NOT NULL,
    resource_url  VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson
    FOREIGN KEY (lesson_id) REFERENCES lessons (id)
);

CREATE INDEX IF NOT EXISTS idx_resource_lesson_id
    ON resources (lesson_id);

CREATE TABLE IF NOT EXISTS reviews (
    id            SERIAL PRIMARY KEY,
    course_id     INT NOT NULL,
    user_id       INT NOT NULL,
    rating        INT NOT NULL,
    comment       TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_review_course_id_user_id
    ON reviews (course_id, user_id);

CREATE TABLE IF NOT EXISTS enrollments (
    id                  SERIAL PRIMARY KEY,
    course_id           INT NOT NULL,
    student_id          INT NOT NULL,
    enrollment_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    certificate_url     VARCHAR(255),
    progress_percent    INT NOT NULL,
    is_completed        VARCHAR(5) NOT NULL,
    CONSTRAINT fk_enrollment_user_course UNIQUE (student_id, course_id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (student_id) REFERENCES users (id)
);

CREATE INDEX IF NOT EXISTS idx_enrollment_course_id_student_id
    ON enrollments (course_id, student_id);
