package ch.example.musicapi.security;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/sql/reset-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SecurityIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setupPasswords() {
    String encodedPassword = passwordEncoder.encode("password123");

    jdbcTemplate.update(
        "UPDATE app_user SET password = ? WHERE username = ?", encodedPassword, "admin");
    jdbcTemplate.update(
        "UPDATE app_user SET password = ? WHERE username = ?", encodedPassword, "editor");
  }

  @Test
  @DisplayName("POST /auth/login returns JWT for valid admin credentials")
  void login_withValidAdminCredentials_returnsToken() throws Exception {
    String requestBody =
        """
                {
                  "username": "admin",
                  "password": "password123"
                }
                """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token", notNullValue()))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.username").value("admin"))
        .andExpect(jsonPath("$.role").value("ADMIN"));
  }

  @Test
  @DisplayName("POST /auth/login returns 401 for wrong password")
  void login_withWrongPassword_returnsUnauthorized() throws Exception {
    String requestBody =
        """
                {
                  "username": "admin",
                  "password": "wrongpassword"
                }
                """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Authentication failed"));
  }

  @Test
  @DisplayName("GET /albums without JWT returns 401")
  void getAlbums_withoutJwt_returnsUnauthorized() throws Exception {
    mockMvc
        .perform(get("/albums"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Unauthorized"));
  }

  @Test
  @DisplayName("GET /albums with invalid JWT returns 401")
  void getAlbums_withInvalidJwt_returnsUnauthorized() throws Exception {
    mockMvc
        .perform(get("/albums").header("Authorization", "Bearer invalid.token.value"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Unauthorized"));
  }

  @Test
  @DisplayName("GET /albums with valid editor JWT returns 200")
  void getAlbums_withValidEditorJwt_returnsOk() throws Exception {
    String loginBody =
        """
                {
                  "username": "editor",
                  "password": "password123"
                }
                """;

    String tokenResponse =
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String jwt = extractToken(tokenResponse);

    mockMvc
        .perform(get("/albums").header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /albums/{id} with editor JWT returns 403")
  void deleteAlbum_withEditorJwt_returnsForbidden() throws Exception {
    String loginBody =
        """
                {
                  "username": "editor",
                  "password": "password123"
                }
                """;

    String tokenResponse =
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String jwt = extractToken(tokenResponse);

    mockMvc
        .perform(delete("/albums/1").header("Authorization", "Bearer " + jwt))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Forbidden"));
  }

  @Test
  @DisplayName("DELETE /albums/{id} with admin JWT returns 204")
  void deleteAlbum_withAdminJwt_returnsNoContent() throws Exception {
    String loginBody =
        """
                {
                  "username": "admin",
                  "password": "password123"
                }
                """;

    String tokenResponse =
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String jwt = extractToken(tokenResponse);

    mockMvc
        .perform(delete("/albums/1").header("Authorization", "Bearer " + jwt))
        .andExpect(status().isNoContent());
  }

  private String extractToken(String json) {
    String marker = "\"token\":\"";
    int start = json.indexOf(marker);
    if (start < 0) {
      throw new IllegalStateException("Token not found in login response: " + json);
    }
    start += marker.length();
    int end = json.indexOf("\"", start);
    if (end < 0) {
      throw new IllegalStateException("Token end not found in login response: " + json);
    }
    return json.substring(start, end);
  }
}
