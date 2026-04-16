package ch.example.musicapi.repository;

import ch.example.musicapi.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByReleaseDate(LocalDate releaseDate);
}