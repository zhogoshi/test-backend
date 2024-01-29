UPDATE budget
SET type = 'Расход'
WHERE type = 'Комиссия';

ALTER TABLE budget
    ADD author_id int default null;

CREATE TABLE author
(
    id         serial PRIMARY KEY,
    name       varchar(128) UNIQUE NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);