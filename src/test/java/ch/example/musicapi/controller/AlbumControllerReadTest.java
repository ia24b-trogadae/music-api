package ch.example.musicapi.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "/sql/reset-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AlbumControllerReadTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /albums/ping returns API is running")
  void ping_returnsApiIsRunning() throws Exception {
    mockMvc
        .perform(get("/albums/ping"))
        .andExpect(status().isOk())
        .andExpect(content().string("API is running"));
  }

  @Test
  @DisplayName("GET /albums returns all albums")
  void getAllAlbums_returnsAllAlbums() throws Exception {
    mockMvc
        .perform(get("/albums"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].title", is("Future Echoes")))
        .andExpect(jsonPath("$[0].songs", hasSize(2)))
        .andExpect(jsonPath("$[1].title", is("Neon Dreams")));
  }

  @Test
  @DisplayName("GET /albums/{id} returns album when it exists")
  void getAlbumById_whenExists_returnsAlbum() throws Exception {
    mockMvc
        .perform(get("/albums/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.title", is("Future Echoes")))
        .andExpect(jsonPath("$.songs", hasSize(2)));
  }

  @Test
  @DisplayName("GET /albums/{id} returns 404 when album does not exist")
  void getAlbumById_whenNotExists_returnsNotFound() throws Exception {
    mockMvc.perform(get("/albums/999")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /albums/count returns album count")
  void countAlbums_returnsCount() throws Exception {
    mockMvc
        .perform(get("/albums/count"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(2)));
  }

  @Test
  @DisplayName("GET /albums/by-date returns albums for matching release date")
  void getAlbumsByReleaseDate_whenMatchExists_returnsAlbums() throws Exception {
    mockMvc
        .perform(get("/albums/by-date").param("releaseDate", "2024-03-15"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].title", is("Future Echoes")));
  }

  @Test
  @DisplayName("GET /albums/by-date returns empty list when no album matches")
  void getAlbumsByReleaseDate_whenNoMatch_returnsEmptyList() throws Exception {
    mockMvc
        .perform(get("/albums/by-date").param("releaseDate", "2000-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }
}
