package ist.cmu.server.service;

import ist.cmu.server.model.Album;
import ist.cmu.server.model.Catalog;
import ist.cmu.server.model.User;
import ist.cmu.server.model.WifiAlbum;
import ist.cmu.server.repository.AlbumRepository;
import ist.cmu.server.repository.CatalogRepository;
import ist.cmu.server.repository.UserRepository;
import ist.cmu.server.repository.WifiAlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("userService")
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private AlbumRepository albumRepository;
    private CatalogRepository catalogRepository;
    private WifiAlbumRepository wifiAlbumRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AlbumRepository albumRepository, CatalogRepository catalogRepository, WifiAlbumRepository wifiAlbumRepository) {
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.catalogRepository = catalogRepository;
        this.wifiAlbumRepository = wifiAlbumRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean addUser(String username, String password, String token) {
        if (getUserByUsername(username) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(1);
        user.setRole("MEMBER");
        user.setDbToken(token);
        userRepository.save(user);
        logger.warn(user.getAlbums().toString());
        return true;
    }

    public boolean createAlbum(String name, User user) {
        Album album = new Album();
        if (albumRepository.findAlbumsByName(name) != null) {
            return false;
        }
        album.setName(name);
        user.getAlbums().add(album);
        logger.warn(user.getAlbums().toString());
        albumRepository.save(album);
        return true;
    }

    public boolean createWifiAlbum(String name, User user) {
        WifiAlbum wifiAlbum = new WifiAlbum();
        if (wifiAlbumRepository.findWifiAlbumByName(name) != null) {
            return false;
        }
        wifiAlbum.setName(name);
        user.getWifiAlbums().add(wifiAlbum);
        wifiAlbumRepository.save(wifiAlbum);
        return true;
    }

    public void addCatalogToAlbum(String albumName, String photosUrl, User user) {
        Album album = albumRepository.findAlbumsByName(albumName);
        if (user.getAlbums().contains(album)) {
            Catalog catalog = new Catalog();
            catalog.setLink(photosUrl);
            catalog.setUser(user.getUsername());
            catalogRepository.save(catalog);
            album.getAlbum_catalogs().add(catalog);
            albumRepository.save(album);
        }
    }

    public Set<String> getAlbumCatalog(String albumName, User user) {
        Album album = albumRepository.findAlbumsByName(albumName);
        if (user.getAlbums().contains(album)) {
            if (album != null) {
                List<Catalog> set = album.getAlbum_catalogs();
                logger.warn(set.toString());
                Set<String> returnSet = new HashSet<>();
                for (Catalog catalog : set) {
                    returnSet.add(catalog.getLink());
                }
                return returnSet;
            }
        }

        return null;
    }

    public String getMyAlbumCatalog(String name, User user) {
        Album album = albumRepository.findAlbumsByName(name);
        if (user.getAlbums().contains(album)) {
            List<Catalog> catalogs = album.getAlbum_catalogs();
            for (Catalog catalog : catalogs) {
                if (catalog.getUser().equals(user.getUsername())) {
                    return catalog.getLink();
                }
            }
        }
        return null;
    }

    public String addUserToAlbum(User user, String username, String albumName) {
        Album album = albumRepository.findAlbumsByName(albumName);
        if (user.getAlbums().contains(album)) {
            User targetUser = userRepository.findByUsername(username);
            if (targetUser == null) {
                return "User doesn't exist";
            } else if (!targetUser.getAlbums().contains(album)) {
                targetUser.getAlbums().add(album);
                userRepository.save(targetUser);
                return "Album added to user " + username;
            } else {
                return "User already in this album";
            }
        } else {
            return "Invalid operation, you don't own this album";
        }
    }

    public String addUserToWifiAlbum(User user, String username, String albumName) {
        WifiAlbum album = wifiAlbumRepository.findWifiAlbumByName(albumName);
        if (user.getWifiAlbums().contains(album)) {
            User targetUser = userRepository.findByUsername(username);
            if (targetUser == null) {
                return "User doesn't exist";
            } else if (!targetUser.getWifiAlbums().contains(album)) {
                targetUser.getWifiAlbums().add(album);
                userRepository.save(targetUser);
                return "Album added to user " + username;
            } else {
                return "User already in this album";
            }
        } else {
            return "Invalid operation, you don't own this album";
        }
    }

    public Boolean hasWifiAlbumsInCommon(String username, User user, String albumName) {
        User user2 = userRepository.findByUsername(username);
        WifiAlbum wifiAlbum = wifiAlbumRepository.findWifiAlbumByName(albumName);
        return user2 != null && wifiAlbum != null && user.getWifiAlbums().contains(wifiAlbum) && user2.getWifiAlbums().contains(wifiAlbum);
    }
}
