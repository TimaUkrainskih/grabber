CREATE TABLE IF NOT EXISTS post (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    link TEXT,
    description TEXT,
    created TIMESTAMP NOT NULL
);
