CREATE TABLE IF NOT EXISTS role
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS album
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(100) NOT NULL,
    artist       VARCHAR(100) NOT NULL,
    genre        VARCHAR(50),
    release_date DATE,
    price        DECIMAL(10, 2),
    track_count  INT,
    published    BOOLEAN,
    CONSTRAINT chk_album_price CHECK (price >= 0),
    CONSTRAINT chk_album_track_count CHECK (track_count >= 1)
);

CREATE TABLE IF NOT EXISTS song
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(100) NOT NULL,
    duration_seconds INT,
    featuring        VARCHAR(100),
    is_explicit      BOOLEAN,
    album_id         INT          NOT NULL,
    CONSTRAINT chk_song_duration CHECK (duration_seconds >= 1),
    CONSTRAINT fk_song_album
        FOREIGN KEY (album_id) REFERENCES album (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS app_user
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN      NOT NULL,
    role_id  INT          NOT NULL,
    CONSTRAINT fk_user_role
        FOREIGN KEY (role_id) REFERENCES role (id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
);