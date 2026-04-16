package ch.example.musicapi.services;

import ch.example.musicapi.dao.AlbumDao;
import ch.example.musicapi.model.Album;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumDao albumDao;

    public AlbumService(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    public List<Album> getAllAlbums() {
        return albumDao.findAll();
    }

    public Optional<Album> getAlbumById(Integer id) {
        return albumDao.findById(id);
    }

    public List<Album> getAlbumsByReleaseDate(LocalDate releaseDate) {
        return albumDao.findByReleaseDate(releaseDate);
    }

    public long countAlbums() {
        return albumDao.count();
    }

    public Album createAlbum(Album album) {
        return albumDao.insert(album);
    }

    public Optional<Album> updateAlbum(Integer id, Album album) {
        if (!albumDao.existsById(id)) {
            return Optional.empty();
        }
        return Optional.of(albumDao.update(id, album));
    }

    public boolean deleteAlbumById(Integer id) {
        if (!albumDao.existsById(id)) {
            return false;
        }
        albumDao.deleteById(id);
        return true;
    }

    public void deleteAllAlbums() {
        albumDao.deleteAll();
    }
}