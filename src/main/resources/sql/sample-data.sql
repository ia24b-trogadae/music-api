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

INSERT INTO app_user (id, username, password, enabled, role_id)
VALUES (1, 'admin', '$2a$10$NIvaABCxgICWOEKsz2cjy.BB9aTLjywpmHH5dDSYRkVcQMOflbdGS', true, 1);

INSERT INTO app_user (id, username, password, enabled, role_id)
VALUES (2, 'editor', '$2a$10$NIvaABCxgICWOEKsz2cjy.BB9aTLjywpmHH5dDSYRkVcQMOflbdGS', true, 2);