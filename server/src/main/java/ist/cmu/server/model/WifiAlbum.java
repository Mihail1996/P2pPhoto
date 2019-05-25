package ist.cmu.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wifiAlbums")

public class WifiAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wifiAlbum_id")
    private int wifiAlbumId;

    @Column(name = "name")
    private String name;

}
