package ist.cmu.server.repository;

import ist.cmu.server.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("albumRepository")
public interface AlbumRepository extends JpaRepository<Album, String> {
    Album findAlbumsByName(String name);

}