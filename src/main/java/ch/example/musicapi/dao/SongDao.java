package ch.example.musicapi.dao;

import ch.example.musicapi.model.Song;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SongDao {

    private final JdbcTemplate jdbcTemplate;

    public SongDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Song> findByAlbumId(Integer albumId) {
        String sql = """
                SELECT id, title, duration_seconds, featuring, is_explicit, album_id
                FROM song
                WHERE album_id = ?
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Song song = new Song();
            song.setId(rs.getInt("id"));
            song.setTitle(rs.getString("title"));
            song.setDurationSeconds(rs.getInt("duration_seconds"));
            song.setFeaturing(rs.getString("featuring"));
            song.setIsExplicit(rs.getBoolean("is_explicit"));
            song.setAlbumId(rs.getInt("album_id"));
            return song;
        }, albumId);
    }

    public void insertSongsForAlbum(Integer albumId, List<Song> songs) {
        String sql = """
                INSERT INTO song (title, duration_seconds, featuring, is_explicit, album_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        for (Song song : songs) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, song.getTitle());
                ps.setInt(2, song.getDurationSeconds());
                ps.setString(3, song.getFeaturing());
                ps.setBoolean(4, song.getIsExplicit());
                ps.setInt(5, albumId);
                return ps;
            });
        }
    }

    public void deleteByAlbumId(Integer albumId) {
        String sql = "DELETE FROM song WHERE album_id = ?";
        jdbcTemplate.update(sql, albumId);
    }
}