-- ============================================
-- CLASSES & AI-GENERATED QUIZ SYSTEM
-- Smart University - Hackathon 24h Extension
-- ============================================

CREATE TABLE users
(
    id         uuid        DEFAULT gen_random_uuid()            NOT NULL,
    email      varchar(255)                                     NOT NULL,
    "password" varchar(255)                                     NOT NULL,
    full_name  varchar(255)                                     NULL,
    "role"     varchar(50) DEFAULT 'student'::character varying NULL,
    is_enabled bool        DEFAULT true                         NULL,
    metadata   jsonb       DEFAULT '{}'::jsonb                  NULL,
    created_at timestamp   DEFAULT CURRENT_TIMESTAMP            NULL,
    updated_at timestamp   DEFAULT CURRENT_TIMESTAMP            NULL,
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_users_email ON public.users USING btree (email);
CREATE INDEX idx_users_role ON public.users USING btree (role);
-- Removed idx_users_username: no `username` column exists in this table. Use `email` as username instead.

-- Classes table (lớp học)
CREATE TABLE classes
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    created_by  UUID REFERENCES users (id) ON DELETE CASCADE,
    -- Students & enrollment
    student_ids UUID[]           DEFAULT '{}', -- Array of student user IDs

    -- Class metadata
    metadata    JSONB            DEFAULT '{
      "meeting_schedule": null,
      "room": null,
      "credits": 3
    }'::JSONB,

    is_active   BOOLEAN          DEFAULT true,
    deleted_at  TIMESTAMP    NULL,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- Documents table (tài liệu lớp học)
CREATE TABLE documents
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id       UUID REFERENCES classes (id) ON DELETE CASCADE,
    uploaded_by    UUID         REFERENCES users (id) ON DELETE SET NULL,

    -- File info
    file_name      VARCHAR(255) NOT NULL,
    file_path      TEXT         NOT NULL, -- S3/local storage path
    file_type      VARCHAR(50)  NOT NULL, -- pdf, docx, pptx, txt
    file_url       TEXT,                  -- Public URL if available

    -- Content extraction (AI processing)
    extracted_text TEXT,                  -- Full text extracted from document

    is_active      BOOLEAN          DEFAULT true,
    deleted_at     TIMESTAMP    NULL,
    created_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- Class enrollments (many-to-many: students <-> classes)
-- Simplified: using array in classes table, but this is for detailed tracking
CREATE TABLE class_enrollments
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id          UUID REFERENCES classes (id) ON DELETE CASCADE,
    student_id        UUID REFERENCES users (id) ON DELETE CASCADE,

    enrollment_status VARCHAR(50)      DEFAULT 'active', -- active, inactive
    enrollment_date   TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    completion_date   TIMESTAMP,

    -- Student performance in class
    stats             JSONB            DEFAULT '{
      "quizzes_completed": 0,
      "avg_score": 0,
      "total_attempts": 0
    }'::JSONB,

    created_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (class_id, student_id)
);

-- public.quiz_attempts definition

-- Drop table

-- DROP TABLE quiz_attempts;

CREATE TABLE quiz_attempts
(
    id           uuid      DEFAULT gen_random_uuid() NOT NULL,
    quiz_id      uuid                                NULL,
    user_id      uuid                                NULL,
    start_time   timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    end_time     timestamp                           NULL,
    expires_at   timestamp                           NULL,
    answers      jsonb     DEFAULT '{}'::jsonb       NULL,
    skills_point jsonb     DEFAULT '{}'::jsonb       NULL,
    score        float8                              NULL,
    created_at   timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at   timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    CONSTRAINT quiz_attempts_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_attempts_answers ON public.quiz_attempts USING gin (answers);
CREATE INDEX idx_attempts_quiz_id ON public.quiz_attempts USING btree (quiz_id);
CREATE INDEX idx_attempts_quiz_user ON public.quiz_attempts USING btree (quiz_id, user_id);
CREATE INDEX idx_attempts_start_time ON public.quiz_attempts USING btree (start_time DESC);
CREATE INDEX idx_attempts_user_id ON public.quiz_attempts USING btree (user_id);


CREATE TABLE quizzes
(
    id               uuid      DEFAULT gen_random_uuid() NOT NULL,
    title            varchar(500)                        NOT NULL,
    skills           TEXT[]    DEFAULT '{}'::TEXT[]      NOT NULL,
    created_by       uuid                                NULL,
    duration_minutes int4                                NULL,
    total_points     float8    DEFAULT 100               NULL,
    questions        jsonb     DEFAULT '[]'::jsonb       NULL,
    stats            jsonb     DEFAULT '{
      "avg_score": 0,
      "pass_rate": 0,
      "total_attempts": 0
    }'::jsonb                                            NULL,
    is_published     bool      DEFAULT false             NULL,
    deleted_at       timestamp                           NULL,
    created_at       timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    updated_at       timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    class_id         uuid                                NULL,
    CONSTRAINT quizzes_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_quizzes_class_id ON public.quizzes USING btree (class_id);
CREATE INDEX idx_quizzes_created_by ON public.quizzes USING btree (created_by);
CREATE INDEX idx_quizzes_deleted_at ON public.quizzes USING btree (deleted_at);
CREATE INDEX idx_quizzes_is_published ON public.quizzes USING btree (is_published) WHERE (deleted_at IS NULL);
CREATE INDEX idx_quizzes_questions ON public.quizzes USING gin (questions);