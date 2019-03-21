CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    body TEXT NOT NULL,
    received TIMESTAMP NOT NULL
);