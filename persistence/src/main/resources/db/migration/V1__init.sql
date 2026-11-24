CREATE TABLE books (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    author        VARCHAR(255) NOT NULL,
    publish_year  INTEGER
);

CREATE TABLE users (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username           VARCHAR(100) NOT NULL,
    password           VARCHAR(255) NOT NULL,
    role               VARCHAR(20)  NOT NULL,
    enabled            BOOLEAN      NOT NULL DEFAULT TRUE,
    confirmation_code  VARCHAR(64),
    CONSTRAINT uq_users_username UNIQUE (username)
);

CREATE TABLE comments (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text        VARCHAR(2000) NOT NULL,
    created_at  TIMESTAMP     NOT NULL,
    book_id     BIGINT        NOT NULL,
    user_id     BIGINT        NOT NULL,
    CONSTRAINT fk_comments_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO users (username, password, role, enabled, confirmation_code)
VALUES ('admin',
        '$2a$10$Y9IpbmiL.icILKMgANIj.OcjwrThDaKXx07o.3UG50WoUzJ9E9lmS',
        'ADMIN',
        TRUE,
        NULL);

INSERT INTO users (username, password, role, enabled, confirmation_code)
VALUES ('user',
        '$2a$10$8h.4bwqLNfem7OG7Dn3wNeuLKvFNHvmpWpobmJOOIbIPBn5/.bKDG',
        'USER',
        TRUE,
        NULL);