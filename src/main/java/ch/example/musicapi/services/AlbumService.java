package ch.example.musicapi.services;

import ch.example.musicapi.dao.AlbumDao;
import ch.example.musicapi.model.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);

    private final AlbumDao albumDao;

    public AlbumService(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    public List<Album> getAllAlbums() {
        List<Album> albums = albumDao.findAll();
        logger.debug("Service loaded {} albums from DAO", albums.size());
        return albums;
    }

    public Optional<Album> getAlbumById(Integer id) {
        Optional<Album> album = albumDao.findById(id);
        if (album.isPresent()) {
            logger.debug(
                    "Service loaded album: id={}, title='{}'", album.get().getId(), album.get().getTitle());
        } else {
            logger.debug("Service could not find album with id {}", id);
        }
        return album;
    }

    public List<Album> getAlbumsByReleaseDate(LocalDate releaseDate) {
        List<Album> albums = albumDao.findByReleaseDate(releaseDate);
        logger.debug("Service loaded {} albums for release date {}", albums.size(), releaseDate);
        return albums;
    }

    public long countAlbums() {
        long count = albumDao.count();
        logger.debug("Service counted {} albums", count);
        return count;
    }

    public Album createAlbum(Album album) {
        Album savedAlbum = albumDao.insert(album);
        logger.debug(
                "Service created album: id={}, title='{}'", savedAlbum.getId(), savedAlbum.getTitle());
        return savedAlbum;
    }

    public Optional<Album> updateAlbum(Integer id, Album album) {
        if (!albumDao.existsById(id)) {
            logger.warn("Album with id {} does not exist", id);
            return Optional.empty();
        }
        Album updatedAlbum = albumDao.update(id, album);
        logger.debug(
                "Service updated album: id={}, title='{}'", updatedAlbum.getId(), updatedAlbum.getTitle());
        return Optional.of(updatedAlbum);
    }

    public boolean deleteAlbumById(Integer id) {
        if (!albumDao.existsById(id)) {
            logger.warn("Album with id {} does not exist", id);
            return false;
        }
        albumDao.deleteById(id);
        logger.debug("Service deleted album with id {}", id);
        return true;
    }

    public void deleteAllAlbums() {
        logger.debug("Service deleting all albums");
        albumDao.deleteAll();
    }
}
