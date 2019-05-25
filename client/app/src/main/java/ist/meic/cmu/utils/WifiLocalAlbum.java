package ist.meic.cmu.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiLocalAlbum implements Serializable {
    private String name;
    private Map<String, Photo> catalog = new HashMap<>();
    private List<String> hashList = new ArrayList<>();


    public List<String> getHashList() {
        return hashList;
    }

    public void setHashList(List<String> hashList) {
        this.hashList = hashList;
    }

    public WifiLocalAlbum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Photo> getCatalog() {
        return catalog;
    }

    public void setCatalog(Map<String, Photo> catalog) {
        this.catalog = catalog;
    }
}
