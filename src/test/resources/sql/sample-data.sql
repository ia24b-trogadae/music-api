INSERT INTO role (id, name) VALUES (1, 'ADMIN');
INSERT INTO role (id, name) VALUES (2, 'EDITOR');

INSERT INTO album (id, title, artist, genre, release_date, price, track_count, published)
VALUES (1, 'Future Echoes', 'Nova Line', 'Pop', '2024-03-15', 19.90, 2, true);

INSERT INTO album (id, title, artist, genre, release_date, price, track_count, published)
VALUES (2, 'Neon Dreams', 'Sky Thread', 'Electronic', '2023-11-02', 24.50, 2, true);

INSERT INTO song (id, title, duration_seconds, featuring, is_explicit, album_id)
VALUES (1, 'Midnight Signal', 210, 'Ari Lux', false, 1);

INSERT INTO song (id, title, duration_seconds, featuring, is_explicit, album_id)
VALUES (2, 'Glass Horizon', 185, NULL, false, 1);

INSERT INTO song (id, title, duration_seconds, featuring, is_explicit, album_id)
VALUES (3, 'Static Heart', 198, 'Mila V', true, 2);

INSERT INTO song (id, title, duration_seconds, featuring, is_explicit, album_id)
VALUES (4, 'Silver Noise', 230, NULL, false, 2);

INSERT INTO app_user (id, username, password, enabled, role_id)
VALUES (1, 'admin', 'dummyhash', true, 1);

INSERT INTO app_user (id, username, password, enabled, role_id)
VALUES (2, 'editor', 'dummyhash', true, 2);