package ch.example.musicapi.dao;

import ch.example.musicapi.model.Album;
import ch.example.musicapi.model.Song;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumDao {

    private final JdbcTemplate jdbcTemplate;
    private final SongDao songDao;

    public AlbumDao(JdbcTemplate jdbcTemplate, SongDao songDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.songDao = songDao;
    }

    public List<Album> findAll() {
        String sql =
                """
                        SELECT id, title, artist, genre, release_date, price, track_count, published
                        FROM album
                        ORDER BY id
                        """;

        List<Album> albums =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {
                            Album album = new Album();
                            album.setId(rs.getInt("id"));
                            album.setTitle(rs.getString("title"));
                            album.setArtist(rs.getString("artist"));
                            album.setGenre(rs.getString("genre"));
                            album.setReleaseDate(rs.getObject("release_date", LocalDate.class));
                            album.setPrice(rs.getBigDecimal("price"));
                            album.setTrackCount(rs.getInt("track_count"));
                            album.setPublished(rs.getBoolean("published"));
                            return album;
                        });

        for (Album album : albums) {
            album.setSongs(songDao.findByAlbumId(album.getId()));
        }

        return albums;
    }

    public Optional<Album> findById(Integer id) {
        String sql =
                """
                        SELECT id, title, artist, genre, release_date, price, track_count, published
                        FROM album
                        WHERE id = ?
                        """;

        List<Album> results =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {
                            Album album = new Album();
                            album.setId(rs.getInt("id"));
                            album.setTitle(rs.getString("title"));
                            album.setArtist(rs.getString("artist"));
                            album.setGenre(rs.getString("genre"));
                            album.setReleaseDate(rs.getObject("release_date", LocalDate.class));
                            album.setPrice(rs.getBigDecimal("price"));
                            album.setTrackCount(rs.getInt("track_count"));
                            album.setPublished(rs.getBoolean("published"));
                            return album;
                        },
                        id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Album album = results.get(0);
        album.setSongs(songDao.findByAlbumId(album.getId()));
        return Optional.of(album);
    }

    public List<Album> findByReleaseDate(LocalDate releaseDate) {
        String sql =
                """
                        SELECT id, title, artist, genre, release_date, price, track_count, published
                        FROM album
                        WHERE release_date = ?
                        ORDER BY id
                        """;

        List<Album> albums =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {
                            Album album = new Album();
                            album.setId(rs.getInt("id"));
                            album.setTitle(rs.getString("title"));
                            album.setArtist(rs.getString("artist"));
                            album.setGenre(rs.getString("genre"));
                            album.setReleaseDate(rs.getObject("release_date", LocalDate.class));
                            album.setPrice(rs.getBigDecimal("price"));
                            album.setTrackCount(rs.getInt("track_count"));
                            album.setPublished(rs.getBoolean("published"));
                            return album;
                        },
                        Date.valueOf(releaseDate));

        for (Album album : albums) {
            album.setSongs(songDao.findByAlbumId(album.getId()));
        }

        return albums;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM album";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    public Album insert(Album album) {
        String sql =
                """
                        INSERT INTO album (title, artist, genre, release_date, price, track_count, published)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, album.getTitle());
                    ps.setString(2, album.getArtist());
                    ps.setString(3, album.getGenre());
                    ps.setDate(4, Date.valueOf(album.getReleaseDate()));
                    ps.setBigDecimal(5, album.getPrice());
                    ps.setInt(6, album.getTrackCount());
                    ps.setBoolean(7, album.getPublished());
                    return ps;
                },
                keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated album id");
        }

        int albumId = key.intValue();
        album.setId(albumId);

        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            songDao.insertSongsForAlbum(albumId, album.getSongs());
            for (Song song : album.getSongs()) {
                song.setAlbumId(albumId);
            }
        }

        return findById(albumId)
                .orElseThrow(() -> new IllegalStateException("Inserted album could not be reloaded"));
    }

    public Album update(Integer id, Album album) {
        String sql =
                """
                        UPDATE album
                        SET title = ?, artist = ?, genre = ?, release_date = ?, price = ?, track_count = ?, published = ?
                        WHERE id = ?
                        """;

        int updatedRows =
                jdbcTemplate.update(
                        sql,
                        album.getTitle(),
                        album.getArtist(),
                        album.getGenre(),
                        Date.valueOf(album.getReleaseDate()),
                        album.getPrice(),
                        album.getTrackCount(),
                        album.getPublished(),
                        id);

        if (updatedRows == 0) {
            throw new IllegalStateException("Album not found for update");
        }

        songDao.deleteByAlbumId(id);

        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            songDao.insertSongsForAlbum(id, album.getSongs());
        }

        return findById(id)
                .orElseThrow(() -> new IllegalStateException("Updated album could not be reloaded"));
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM album WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public void deleteById(Integer id) {
        songDao.deleteByAlbumId(id);
        String sql = "DELETE FROM album WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM song");
        jdbcTemplate.update("DELETE FROM album");
    }
}
