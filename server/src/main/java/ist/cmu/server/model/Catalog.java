package ist.cmu.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "catalogs")

public class Catalog {
    @Id
    @Column(name = "link")
    private String link;

    @Column(name = "user")
    private String user;
}
