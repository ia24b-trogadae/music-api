package ch.example.musicapi.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DatabaseInitializerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseInitializer databaseInitializer;

    @Test
    @DisplayName("Startup script creates tables and inserts sample data")
    void startupScript_createsTablesAndInsertsSampleData() {
        Integer albumCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM album", Integer.class);
        Integer songCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM song", Integer.class);
        Integer roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM role", Integer.class);
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);

        assertEquals(2, albumCount);
        assertEquals(4, songCount);
        assertEquals(2, roleCount);
        assertEquals(2, userCount);
    }

    @Test
    @DisplayName("Startup script does not duplicate sample data when database is already initialized")
    void startupScript_whenDatabaseAlreadyInitialized_doesNotDuplicateData() throws Exception {
        Integer albumCountBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM album", Integer.class);
        Integer songCountBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM song", Integer.class);
        Integer roleCountBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM role", Integer.class);
        Integer userCountBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);

        databaseInitializer.run();

        Integer albumCountAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM album", Integer.class);
        Integer songCountAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM song", Integer.class);
        Integer roleCountAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM role", Integer.class);
        Integer userCountAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);

        assertEquals(albumCountBefore, albumCountAfter);
        assertEquals(songCountBefore, songCountAfter);
        assertEquals(roleCountBefore, roleCountAfter);
        assertEquals(userCountBefore, userCountAfter);
    }

    @Test
    @DisplayName("Startup script creates all required tables")
    void startupScript_createsAllRequiredTables() {
        Integer albumTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = 'album'",
                Integer.class
        );
        Integer songTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = 'song'",
                Integer.class
        );
        Integer roleTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = 'role'",
                Integer.class
        );
        Integer userTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = 'app_user'",
                Integer.class
        );

        assertTrue(albumTable != null && albumTable > 0);
        assertTrue(songTable != null && songTable > 0);
        assertTrue(roleTable != null && roleTable > 0);
        assertTrue(userTable != null && userTable > 0);
    }
}