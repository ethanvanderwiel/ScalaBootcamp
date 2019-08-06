CREATE TABLE users (
    username varchar,
    password varchar
);

CREATE TABLE searches (
    username varchar,
    searchstring varchar
);

CREATE TABLE results (
    searchstring varchar,
    title varchar,
    description varchar,
    username varchar
);