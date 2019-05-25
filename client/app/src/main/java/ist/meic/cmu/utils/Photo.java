package ist.meic.cmu.utils;

import java.io.Serializable;

public class Photo implements Serializable {
    private String name;
    private String hash;

    public Photo(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
