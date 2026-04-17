package ch.example.musicapi.services;

import ch.example.musicapi.dao.AlbumDao;
import ch.example.musicapi.model.Album;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumDao albumDao;

    @InjectMocks
    private AlbumService albumService;

    @Test
    @DisplayName("Service returns all albums")
    void getAllAlbums_returnsAllAlbums() {
        Album album = new Album();
        album.setId(1);
        album.setTitle("Test");

        when(albumDao.findAll()).thenReturn(List.of(album));

        List<Album> result = albumService.getAllAlbums();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getTitle());
        verify(albumDao).findAll();
    }

    @Test
    @DisplayName("Service returns album by id when it exists")
    void getAlbumById_whenExists_returnsAlbum() {
        Album album = new Album();
        album.setId(1);

        when(albumDao.findById(1)).thenReturn(Optional.of(album));

        Optional<Album> result = albumService.getAlbumById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    @DisplayName("Service returns empty when album by id does not exist")
    void getAlbumById_whenNotExists_returnsEmpty() {
        when(albumDao.findById(999)).thenReturn(Optional.empty());

        Optional<Album> result = albumService.getAlbumById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Service returns albums by release date")
    void getAlbumsByReleaseDate_returnsAlbums() {
        when(albumDao.findByReleaseDate(LocalDate.of(2024, 1, 1))).thenReturn(List.of(new Album()));

        List<Album> result = albumService.getAlbumsByReleaseDate(LocalDate.of(2024, 1, 1));

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Service returns album count")
    void countAlbums_returnsCount() {
        when(albumDao.count()).thenReturn(2L);

        long result = albumService.countAlbums();

        assertEquals(2L, result);
    }

    @Test
    @DisplayName("Service creates album")
    void createAlbum_returnsCreatedAlbum() {
        Album album = new Album();
        album.setTitle("Created");

        when(albumDao.insert(album)).thenReturn(album);

        Album result = albumService.createAlbum(album);

        assertEquals("Created", result.getTitle());
        verify(albumDao).insert(album);
    }

    @Test
    @DisplayName("Service updates album when it exists")
    void updateAlbum_whenExists_returnsUpdatedAlbum() {
        Album album = new Album();
        album.setTitle("Updated");

        when(albumDao.existsById(1)).thenReturn(true);
        when(albumDao.update(1, album)).thenReturn(album);

        Optional<Album> result = albumService.updateAlbum(1, album);

        assertTrue(result.isPresent());
        assertEquals("Updated", result.get().getTitle());
    }

    @Test
    @DisplayName("Service update returns empty when album does not exist")
    void updateAlbum_whenNotExists_returnsEmpty() {
        Album album = new Album();

        when(albumDao.existsById(999)).thenReturn(false);

        Optional<Album> result = albumService.updateAlbum(999, album);

        assertTrue(result.isEmpty());
        verify(albumDao, never()).update(anyInt(), any());
    }

    @Test
    @DisplayName("Service deletes album when it exists")
    void deleteAlbumById_whenExists_returnsTrue() {
        when(albumDao.existsById(1)).thenReturn(true);

        boolean result = albumService.deleteAlbumById(1);

        assertTrue(result);
        verify(albumDao).deleteById(1);
    }

    @Test
    @DisplayName("Service delete returns false when album does not exist")
    void deleteAlbumById_whenNotExists_returnsFalse() {
        when(albumDao.existsById(999)).thenReturn(false);

        boolean result = albumService.deleteAlbumById(999);

        assertFalse(result);
        verify(albumDao, never()).deleteById(anyInt());
    }
}