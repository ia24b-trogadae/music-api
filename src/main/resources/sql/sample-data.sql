INSERT INTO role (name) VALUES ('ADMIN');
INSERT INTO role (name) VALUES ('EDITOR');

INSERT INTO album (title, artist, genre, release_date, price, track_count, published)
VALUES ('Future Echoes', 'Nova Line', 'Pop', '2024-03-15', 19.90, 2, true);

INSERT INTO album (title, artist, genre, release_date, price, track_count, published)
VALUES ('Neon Dreams', 'Sky Thread', 'Electronic', '2023-11-02', 24.50, 2, true);

INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
VALUES ('Midnight Signal', 210, 'Ari Lux', false, 1);

INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
VALUES ('Static Heart', 198, 'Mila V', true, 2);

INSERT INTO app_user (username, password, enabled, role_id)
VALUES ('admin', '$2a$10$7QJ0m0D0K8lYwY6p7M5Y8uT7A6Q9u2U5jY2V3z5jP2W1fWg4fVw8K', true, 1); -- Passwort ist nur Platzhalter als Hash

INSERT INTO app_user (username, password, enabled, role_id)
VALUES ('editor', '$2a$10$7QJ0m0D0K8lYwY6p7M5Y8uT7A6Q9u2U5jY2V3z5jP2W1fWg4fVw8K', true, 2); -- Passwort ist nur Platzhalter als Hash