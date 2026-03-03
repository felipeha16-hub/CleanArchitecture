-- Migration: Seed initial mock users
-- Version: 2
-- Description: Insert 3 mock users into the users table

INSERT INTO users (username, email, password, pokemons_ids)
VALUES
    ('felipe', 'felipe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm', ARRAY[1, 4, 7]),
    ('maria', 'maria@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm', ARRAY[25, 39, 54]),
    ('carlos', 'carlos@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/KFm', ARRAY[6, 25, 94]);

