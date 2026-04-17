package ch.example.musicapi.dao;

import ch.example.musicapi.model.AppUser;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppUserDao {

  private final JdbcTemplate jdbcTemplate;

  public AppUserDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<AppUser> findByUsername(String username) {
    String sql =
        """
                SELECT u.id, u.username, u.password, u.enabled, u.role_id, r.name AS role_name
                FROM app_user u
                JOIN role r ON u.role_id = r.id
                WHERE u.username = ?
                """;

    List<AppUser> users =
        jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
              AppUser user = new AppUser();
              user.setId(rs.getInt("id"));
              user.setUsername(rs.getString("username"));
              user.setPassword(rs.getString("password"));
              user.setEnabled(rs.getBoolean("enabled"));
              user.setRoleId(rs.getInt("role_id"));
              user.setRoleName(rs.getString("role_name"));
              return user;
            },
            username);

    return users.stream().findFirst();
  }
}
