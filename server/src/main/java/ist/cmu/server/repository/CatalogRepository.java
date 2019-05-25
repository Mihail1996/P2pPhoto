package ist.cmu.server.repository;


import ist.cmu.server.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("catalogRepository")
public interface CatalogRepository extends JpaRepository<Catalog, String> {
    Catalog findCatalogByUser(String user);
}