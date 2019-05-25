package ist.meic.cmu.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WifiMessage implements Serializable {
    private String senderUsername;
    private String albumName;
    private boolean hasAlbumInCommon;
    private String ID;
    private Map<byte[], String> photos = new HashMap<>();

    private List<String> hashList = new ArrayList<>();

    public List<String> getHashList() {
        return hashList;
    }

    public void setHashList(List<String> hashList) {
        this.hashList = hashList;
    }

    public Map<byte[], String> getPhotos() {
        return photos;
    }

    public void setPhotos(Map<byte[], String> photos) {
        this.photos = photos;
    }

    public boolean HasAlbumInCommon() {
        return hasAlbumInCommon;
    }

    public void setHasAlbumInCommon(boolean hasAlbumInCommon) {
        this.hasAlbumInCommon = hasAlbumInCommon;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public WifiMessage(String senderUsername, String albumName) {
        this.senderUsername = senderUsername;
        this.albumName = albumName;
        this.ID = UUID.randomUUID().toString();
    }
}
