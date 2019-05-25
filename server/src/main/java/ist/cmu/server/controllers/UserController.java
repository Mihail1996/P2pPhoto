package ist.cmu.server.controllers;

import ist.cmu.server.model.Album;
import ist.cmu.server.model.User;
import ist.cmu.server.model.WifiAlbum;
import ist.cmu.server.service.LoggerFactory;
import ist.cmu.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/cmu")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/users/{username}", method = RequestMethod.GET)
    public String getUserByCard(@PathVariable final String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return "Username: " + user.getUsername() + "\n" +
                    "Number of albums: " + user.getAlbums().size() + "\n" +
                    "Number of Wifi albums: " + user.getWifiAlbums().size() + "\n";
        }

        return "User not found";
    }

    @RequestMapping(value = "/getDbToken", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getDbToken() {
        LoggerFactory.log("Get DropBox token");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return user.getDbToken();
    }

    @RequestMapping(value = "/createAlbum", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createAlbum(String name) {
        LoggerFactory.log("Create Album " + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.createAlbum(name, user);

    }


    @RequestMapping(value = "/createWifiAlbum", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public boolean createWifiAlbum(String name) {
        LoggerFactory.log("Create Album " + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.createWifiAlbum(name, user);

    }

    @RequestMapping(value = "/getUserAlbums", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<String> getUserAlbums() {
        LoggerFactory.log("Get User albums");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        Set<Album> albums = userService.getUserByUsername(user.getUsername()).getAlbums();
        List<String> albumNames = new ArrayList<>();
        for (Album album : albums) {
            albumNames.add(album.getName());
        }
        return albumNames;
    }

    @RequestMapping(value = "/getUserWifiAlbums", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<String> getUserWifiAlbums() {
        LoggerFactory.log("Get User wifi albums");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        Set<WifiAlbum> albums = userService.getUserByUsername(user.getUsername()).getWifiAlbums();
        List<String> albumNames = new ArrayList<>();
        for (WifiAlbum wifiAlbum : albums) {
            albumNames.add(wifiAlbum.getName());
        }
        return albumNames;
    }

    @RequestMapping(value = "/addCatalogToAlbum", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void addCatalogToAlbum(final String url, final String name) {
        LoggerFactory.log("Add Catalog to album url: " + url + " name: " + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        userService.addCatalogToAlbum(name, url, user);
    }

    @RequestMapping(value = "/addUserToAlbum", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String addUserToAlbum(final String username, final String albumName) {
        LoggerFactory.log("Add user to album username: " + username + " albumName: " + albumName);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.addUserToAlbum(user, username, albumName);
    }

    @RequestMapping(value = "/addUserToWifiAlbum", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String addUserToWifiAlbum(final String username, final String albumName) {
        LoggerFactory.log("Add user to wifi album username: " + username + " albumName: " + albumName);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.addUserToWifiAlbum(user, username, albumName);
    }

    @RequestMapping(value = "/getMyAlbumCatalog", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getMyAlbumCatalog(final String name) {
        LoggerFactory.log("Get my album Catalog name: " + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.getMyAlbumCatalog(name, user);
    }


    @RequestMapping(value = "/getAlbumCatalogs", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Set<String> getAlbumCatalogs(final String name) {
        LoggerFactory.log("Get album Catalogs name " + name);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.getAlbumCatalog(name, user);
    }

    @RequestMapping(value = "/hasWifiAlbumsInCommon", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public boolean hasWifiAlbumsInCommon(final String username, final String albumName) {
        LoggerFactory.log("Get wifi Albums In Common " + username + " " + albumName);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return userService.hasWifiAlbumsInCommon(username, user, albumName);
    }

    @RequestMapping(value = "/getLog", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public byte[] getLog() throws IOException {
        LoggerFactory.log("Get log");
        File file = new File("p2pPhotosLog.txt");
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return bytes;
    }


}

