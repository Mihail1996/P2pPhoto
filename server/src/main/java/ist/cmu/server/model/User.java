package ist.cmu.server.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private int active;

    @Column(name = "role")
    private String role;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_album", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "album_id"))
    @Builder.Default
    private final Set<Album> albums = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_wifiAlbum", joinColumns = @JoinColumn(name = "username"), inverseJoinColumns = @JoinColumn(name = "wifiAlbum_id"))
    @Builder.Default
    private final Set<WifiAlbum> wifiAlbums = new HashSet<>();

    @Column(name = "dbToken")
    private String dbToken;


}