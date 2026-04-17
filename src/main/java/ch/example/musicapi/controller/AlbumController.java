package ch.example.musicapi.controller;

import ch.example.musicapi.model.Album;
import ch.example.musicapi.services.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/albums")
public class AlbumController {

  private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

  private final AlbumService albumService;

  public AlbumController(AlbumService albumService) {
    this.albumService = albumService;
  }

  @Operation(summary = "Ping service", description = "Checks whether the API is running")
  @ApiResponse(responseCode = "200", description = "API is running")
  @GetMapping("/ping")
  public ResponseEntity<String> ping() {
    return ResponseEntity.ok("API is running");
  }

  @Operation(summary = "Get all albums", description = "Returns all albums including their songs")
  @ApiResponse(responseCode = "200", description = "Albums loaded successfully")
  @GetMapping
  public ResponseEntity<List<Album>> getAllAlbums() {
    List<Album> albums = albumService.getAllAlbums();
    logger.info("Fetched {} albums", albums.size());
    return ResponseEntity.ok(albums);
  }

  @Operation(
      summary = "Get album by id",
      description = "Returns one album by its id including songs")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Album found"),
    @ApiResponse(responseCode = "404", description = "Album not found", content = @Content)
  })
  @GetMapping("/{id}")
  public ResponseEntity<Album> getAlbumById(
      @Parameter(description = "Album id", example = "1") @PathVariable Integer id) {
    Optional<Album> album = albumService.getAlbumById(id);
    return album
        .map(
            foundAlbum -> {
              logger.info(
                  "Album found: id={}, title='{}'", foundAlbum.getId(), foundAlbum.getTitle());
              return ResponseEntity.ok(foundAlbum);
            })
        .orElseGet(
            () -> {
              logger.warn("Album with id {} not found", id);
              return ResponseEntity.notFound().build();
            });
  }

  @Operation(summary = "Count albums", description = "Returns the number of albums in the database")
  @ApiResponse(responseCode = "200", description = "Count returned successfully")
  @GetMapping("/count")
  public ResponseEntity<Map<String, Long>> countAlbums() {
    long count = albumService.countAlbums();
    logger.info("Album count requested: {}", count);
    return ResponseEntity.ok(Map.of("count", count));
  }

  @Operation(
      summary = "Get albums by release date",
      description = "Returns all albums with the given release date")
  @ApiResponse(responseCode = "200", description = "Albums filtered successfully")
  @GetMapping("/by-date")
  public ResponseEntity<List<Album>> getAlbumsByReleaseDate(
      @Parameter(description = "Release date in format YYYY-MM-DD", example = "2024-03-15")
          @RequestParam
          LocalDate releaseDate) {
    List<Album> albums = albumService.getAlbumsByReleaseDate(releaseDate);
    logger.info("Fetched {} albums for release date {}", albums.size(), releaseDate);
    return ResponseEntity.ok(albums);
  }

  @Operation(
      summary = "Create a new album",
      description = "Creates a new album including its songs")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Album created successfully"),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
  })
  @PostMapping
  public ResponseEntity<Album> createAlbum(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Album object to create",
              required = true,
              content = @Content(schema = @Schema(implementation = Album.class)))
          @Valid
          @RequestBody
          Album album) {
    Album savedAlbum = albumService.createAlbum(album);
    logger.info("Album created: id={}, title='{}'", savedAlbum.getId(), savedAlbum.getTitle());
    return ResponseEntity.status(201).body(savedAlbum);
  }

  @Operation(summary = "Update an album", description = "Updates an existing album and its songs")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Album updated successfully"),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "404", description = "Album not found", content = @Content)
  })
  @PutMapping("/{id}")
  public ResponseEntity<Album> updateAlbum(
      @Parameter(description = "Album id", example = "1") @PathVariable Integer id,
      @Valid @RequestBody Album album) {
    Optional<Album> updatedAlbum = albumService.updateAlbum(id, album);
    return updatedAlbum
        .map(
            updated -> {
              logger.info("Album updated: id={}, title='{}'", updated.getId(), updated.getTitle());
              return ResponseEntity.ok(updated);
            })
        .orElseGet(
            () -> {
              logger.warn("Album with id {} not found for update", id);
              return ResponseEntity.notFound().build();
            });
  }

  @Operation(summary = "Delete album by id", description = "Deletes one album and its songs by id")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Album deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Album not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAlbumById(
      @Parameter(description = "Album id", example = "1") @PathVariable Integer id) {
    boolean deleted = albumService.deleteAlbumById(id);
    if (!deleted) {
      logger.warn("Album with id {} not found for deletion", id);
      return ResponseEntity.notFound().build();
    }
    logger.info("Album deleted: id={}", id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete all albums", description = "Deletes all albums and all songs")
  @ApiResponse(responseCode = "204", description = "All albums deleted successfully")
  @DeleteMapping
  public ResponseEntity<Void> deleteAllAlbums() {
    long countBeforeDelete = albumService.countAlbums();
    albumService.deleteAllAlbums();
    logger.info("All albums deleted. Deleted count={}", countBeforeDelete);
    return ResponseEntity.noContent().build();
  }
}
