CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    link TEXT UNIQUE NOT NULL,
    description TEXT,
    created TIMESTAMP NOT NULL
);