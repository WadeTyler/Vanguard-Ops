CREATE TABLE users
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- User ID (native UUID)
    username      VARCHAR(255) NOT NULL UNIQUE,               -- Unique username (email)
    password_hash VARCHAR(512) NOT NULL,                      -- Hashed password
    first_name    VARCHAR(255) NOT NULL,                      -- First name
    last_name     VARCHAR(255) NOT NULL,                      -- Last name

    created_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- Authorities lookup for controlled role values
CREATE TABLE authorities
(
    authority VARCHAR(50) PRIMARY KEY
);

CREATE TABLE user_authorities
(
    user_id   UUID        NOT NULL,
    authority VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (authority) REFERENCES authorities (authority) ON DELETE CASCADE
);

COMMIT;

-- Seed initial authorities
INSERT INTO authorities (authority)
VALUES ('ROLE_ADMIN'),
       ('ROLE_TECHNICIAN'),
       ('ROLE_OPERATOR'),
       ('ROLE_PLANNER'),
       ('ROLE_ANALYST');

COMMIT;

-- Indexes
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_user_authorities_user_id ON user_authorities (user_id);

COMMIT;