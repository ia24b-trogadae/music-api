package ch.example.musicapi.controller;

import ch.example.musicapi.model.Album;
import ch.example.musicapi.model.Song;
import ch.example.musicapi.repository.AlbumRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumRepository albumRepository;

    public AlbumController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("API is running");
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() {
        return ResponseEntity.ok(albumRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable Integer id) {
        Optional<Album> album = albumRepository.findById(id);
        return album.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countAlbums() {
        return ResponseEntity.ok(Map.of("count", albumRepository.count()));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Album>> getAlbumsByReleaseDate(@RequestParam LocalDate releaseDate) {
        return ResponseEntity.ok(albumRepository.findByReleaseDate(releaseDate));
    }

    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestBody Album album) {
        setAlbumReferenceInSongs(album);
        Album savedAlbum = albumRepository.save(album);
        return ResponseEntity.status(201).body(savedAlbum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Integer id, @RequestBody Album updatedAlbum) {
        Optional<Album> existingAlbumOptional = albumRepository.findById(id);

        if (existingAlbumOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Album existingAlbum = existingAlbumOptional.get();
        existingAlbum.setTitle(updatedAlbum.getTitle());
        existingAlbum.setArtist(updatedAlbum.getArtist());
        existingAlbum.setGenre(updatedAlbum.getGenre());
        existingAlbum.setReleaseDate(updatedAlbum.getReleaseDate());
        existingAlbum.setPrice(updatedAlbum.getPrice());
        existingAlbum.setTrackCount(updatedAlbum.getTrackCount());
        existingAlbum.setPublished(updatedAlbum.getPublished());

        existingAlbum.getSongs().clear();
        if (updatedAlbum.getSongs() != null) {
            for (Song song : updatedAlbum.getSongs()) {
                song.setAlbum(existingAlbum);
                existingAlbum.getSongs().add(song);
            }
        }

        Album savedAlbum = albumRepository.save(existingAlbum);
        return ResponseEntity.ok(savedAlbum);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbumById(@PathVariable Integer id) {
        if (!albumRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        albumRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAlbums() {
        albumRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    private void setAlbumReferenceInSongs(Album album) {
        if (album.getSongs() != null) {
            for (Song song : album.getSongs()) {
                song.setAlbum(album);
            }
        }
    }
}