-- Create database
CREATE DATABASE oauth_db;

-- Connect to database
\c oauth_db;

-- Create users table (will be created by Hibernate, but keeping for reference)
-- CREATE TABLE IF NOT EXISTS users (
--     id BIGSERIAL PRIMARY KEY,
--     username VARCHAR(255) NOT NULL UNIQUE,
--     password VARCHAR(255) NOT NULL,
--     email VARCHAR(255) NOT NULL,
--     enabled BOOLEAN NOT NULL DEFAULT true
-- );

-- CREATE TABLE IF NOT EXISTS user_roles (
--     user_id BIGINT NOT NULL,
--     role VARCHAR(50) NOT NULL,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-- );
