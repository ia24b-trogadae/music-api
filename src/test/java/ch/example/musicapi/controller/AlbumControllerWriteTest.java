package ch.example.musicapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "/sql/reset-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AlbumControllerWriteTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /albums creates album successfully")
    void createAlbum_withValidBody_returnsCreated() throws Exception {
        String requestBody = """
                {
                  "title": "Crystal Nights",
                  "artist": "Luna Fade",
                  "genre": "Synthpop",
                  "releaseDate": "2024-08-20",
                  "price": 21.50,
                  "trackCount": 2,
                  "published": true,
                  "songs": [
                    {
                      "title": "Velvet Lights",
                      "durationSeconds": 205,
                      "featuring": "Noah Ray",
                      "isExplicit": false
                    },
                    {
                      "title": "Afterglow",
                      "durationSeconds": 190,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("Crystal Nights")))
                .andExpect(jsonPath("$.songs", hasSize(2)));
    }

    @Test
    @DisplayName("POST /albums accepts validation boundary values")
    void createAlbum_withBoundaryValues_returnsCreated() throws Exception {
        String requestBody = """
                {
                  "title": "AB",
                  "artist": "CD",
                  "genre": "Pop",
                  "releaseDate": "2024-01-01",
                  "price": 0.00,
                  "trackCount": 1,
                  "published": true,
                  "songs": [
                    {
                      "title": "EF",
                      "durationSeconds": 1,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price", is(0.00)))
                .andExpect(jsonPath("$.trackCount", is(1)))
                .andExpect(jsonPath("$.songs[0].durationSeconds", is(1)));
    }

    @Test
    @DisplayName("POST /albums returns 400 for negative price")
    void createAlbum_withNegativePrice_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "Bad Album",
                  "artist": "Test Artist",
                  "genre": "Pop",
                  "releaseDate": "2024-01-01",
                  "price": -5.00,
                  "trackCount": 2,
                  "published": true,
                  "songs": [
                    {
                      "title": "Song One",
                      "durationSeconds": 120,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.messages", hasKey("price")));
    }

    @Test
    @DisplayName("POST /albums returns 400 for future release date")
    void createAlbum_withFutureReleaseDate_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "Future Album",
                  "artist": "Test Artist",
                  "genre": "Pop",
                  "releaseDate": "2099-01-01",
                  "price": 10.00,
                  "trackCount": 2,
                  "published": true,
                  "songs": [
                    {
                      "title": "Song One",
                      "durationSeconds": 120,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation failed")))
                .andExpect(jsonPath("$.messages", hasKey("releaseDate")));
    }

    @Test
    @DisplayName("POST /albums returns 400 for invalid track count")
    void createAlbum_withTrackCountZero_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "Invalid Count",
                  "artist": "Test Artist",
                  "genre": "Pop",
                  "releaseDate": "2024-01-01",
                  "price": 10.00,
                  "trackCount": 0,
                  "published": true,
                  "songs": [
                    {
                      "title": "Song One",
                      "durationSeconds": 120,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasKey("trackCount")));
    }

    @Test
    @DisplayName("POST /albums returns 400 for invalid song duration")
    void createAlbum_withSongDurationZero_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "Album Test",
                  "artist": "Test Artist",
                  "genre": "Pop",
                  "releaseDate": "2024-01-01",
                  "price": 10.00,
                  "trackCount": 2,
                  "published": true,
                  "songs": [
                    {
                      "title": "Song One",
                      "durationSeconds": 0,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasKey("songs[0].durationSeconds")));    }

    @Test
    @DisplayName("PUT /albums/{id} updates album successfully")
    void updateAlbum_whenAlbumExists_returnsUpdatedAlbum() throws Exception {
        String requestBody = """
                {
                  "title": "Future Echoes Deluxe",
                  "artist": "Nova Line",
                  "genre": "Pop",
                  "releaseDate": "2024-03-15",
                  "price": 22.90,
                  "trackCount": 2,
                  "published": true,
                  "songs": [
                    {
                      "title": "Midnight Signal Remastered",
                      "durationSeconds": 215,
                      "featuring": "Ari Lux",
                      "isExplicit": false
                    },
                    {
                      "title": "Glass Horizon Live",
                      "durationSeconds": 200,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(put("/albums/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Future Echoes Deluxe")))
                .andExpect(jsonPath("$.price", is(22.90)))
                .andExpect(jsonPath("$.songs[0].title", is("Midnight Signal Remastered")));
    }

    @Test
    @DisplayName("PUT /albums/{id} returns 404 when album does not exist")
    void updateAlbum_whenAlbumDoesNotExist_returnsNotFound() throws Exception {
        String requestBody = """
                {
                  "title": "Missing Album",
                  "artist": "Nobody",
                  "genre": "Pop",
                  "releaseDate": "2024-03-15",
                  "price": 20.00,
                  "trackCount": 1,
                  "published": true,
                  "songs": [
                    {
                      "title": "Only Song",
                      "durationSeconds": 180,
                      "featuring": null,
                      "isExplicit": false
                    }
                  ]
                }
                """;

        mockMvc.perform(put("/albums/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /albums/{id} deletes existing album")
    void deleteAlbumById_whenAlbumExists_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/albums/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/albums/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /albums/{id} returns 404 for missing album")
    void deleteAlbumById_whenAlbumDoesNotExist_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/albums/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /albums deletes all albums")
    void deleteAllAlbums_returnsNoContentAndEmptiesTable() throws Exception {
        mockMvc.perform(delete("/albums"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/albums/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)));
    }
}