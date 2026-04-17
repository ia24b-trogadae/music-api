INSERT INTO role (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1
                  FROM role
                  WHERE name = 'ADMIN');

INSERT INTO role (name)
SELECT 'EDITOR'
WHERE NOT EXISTS (SELECT 1
                  FROM role
                  WHERE name = 'EDITOR');

INSERT INTO album (title, artist, genre, release_date, price, track_count, published)
SELECT 'Future Echoes', 'Nova Line', 'Pop', '2024-03-15', 19.90, 2, true
WHERE NOT EXISTS (SELECT 1
                  FROM album
                  WHERE title = 'Future Echoes'
                    AND artist = 'Nova Line');

INSERT INTO album (title, artist, genre, release_date, price, track_count, published)
SELECT 'Neon Dreams', 'Sky Thread', 'Electronic', '2023-11-02', 24.50, 2, true
WHERE NOT EXISTS (SELECT 1
                  FROM album
                  WHERE title = 'Neon Dreams'
                    AND artist = 'Sky Thread');

INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
SELECT 'Midnight Signal', 210, 'Ari Lux', false, a.id
FROM album a
WHERE a.title = 'Future Echoes'
  AND a.artist = 'Nova Line'
  AND NOT EXISTS (SELECT 1
                  FROM song s
                  WHERE s.title = 'Midnight Signal'
                    AND s.album_id = a.id);

INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
SELECT 'Glass Horizon', 185, NULL, false, a.id
FROM album a
WHERE a.title = 'Future Echoes'
  AND a.artist = 'Nova Line'
  AND NOT EXISTS (SELECT 1
                  FROM song s
                  WHERE s.title = 'Glass Horizon'
                    AND s.album_id = a.id);

INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
SELECT 'Static Heart', 198, 'Mila V', true, a.id
FROM album a
WHERE a.title = 'Neon Dreams'
  AND a.artist = 'Sky Thread'
  AND NOT EXISTS (SELECT 1
                  FROM song s
                  WHERE s.title = 'Static Heart'
                    AND s.album_id = a.id);

INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
SELECT 'Silver Noise', 230, NULL, false, a.id
FROM album a
WHERE a.title = 'Neon Dreams'
  AND a.artist = 'Sky Thread'
  AND NOT EXISTS (SELECT 1
                  FROM song s
                  WHERE s.title = 'Silver Noise'
                    AND s.album_id = a.id);

INSERT INTO app_user (username, password, enabled, role_id)
SELECT 'admin', '$2a$10$NIvaABCxgICWOEKsz2cjy.BB9aTLjywpmHH5dDSYRkVcQMOflbdGS', true, r.id
FROM role r
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1
                  FROM app_user u
                  WHERE u.username = 'admin');

INSERT INTO app_user (username, password, enabled, role_id)
SELECT 'editor', '$2a$10$NIvaABCxgICWOEKsz2cjy.BB9aTLjywpmHH5dDSYRkVcQMOflbdGS', true, r.id
FROM role r
WHERE r.name = 'EDITOR'
  AND NOT EXISTS (SELECT 1
                  FROM app_user u
                  WHERE u.username = 'editor');