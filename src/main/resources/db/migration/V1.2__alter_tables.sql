ALTER TABLE users
    ADD student_id varchar(100) default NULL;
ALTER TABLE users
    ADD CONSTRAINT users_id_unique UNIQUE (student_id);

ALTER TABLE quizzes
    ADD document_url TEXT NOT NULL;