package ist.cmu.server.repository;

import ist.cmu.server.model.WifiAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("wifiAlbumRepository")
public interface WifiAlbumRepository extends JpaRepository<WifiAlbum, String> {
    WifiAlbum findWifiAlbumByName(String name);
}