CREATE TABLE users
(
    id       INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE
);

CREATE TYPE sleep_quality AS ENUM ('GOOD', 'BAD', 'OK');

CREATE TABLE sleep_data
(
    id             INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id        INT           NOT NULL REFERENCES users (id),

    date           DATE          NOT NULL,
    time_start     TIME          NOT NULL,
    duration_hours DECIMAL(4, 2) NOT NULL,

    quality        sleep_quality NOT NULL
);