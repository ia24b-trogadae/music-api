package ch.example.musicapi.controller;

import ch.example.musicapi.model.Album;
import ch.example.musicapi.services.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("API is running");
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.ok(albumService.getAllAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Integer id) {
        Optional<Album> album = albumService.getAlbumById(id);
        return album.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countAlbums() {
        return ResponseEntity.ok(Map.of("count", albumService.countAlbums()));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Album>> getAlbumsByReleaseDate(@RequestParam LocalDate releaseDate) {
        return ResponseEntity.ok(albumService.getAlbumsByReleaseDate(releaseDate));
    }

    @PostMapping
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody Album album) {
        Album savedAlbum = albumService.createAlbum(album);
        return ResponseEntity.status(201).body(savedAlbum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Integer id, @Valid @RequestBody Album album) {
        Optional<Album> updatedAlbum = albumService.updateAlbum(id, album);
        return updatedAlbum.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbumById(@PathVariable Integer id) {
        boolean deleted = albumService.deleteAlbumById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAlbums() {
        albumService.deleteAllAlbums();
        return ResponseEntity.noContent().build();
    }
}