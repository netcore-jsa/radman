package software.netcore.radman.data.radius.entity;

import com.querydsl.core.annotations.QueryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@Entity
@QueryEntity
@Table(name = "nas")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Nas {

    @Id
    @Column(length = 10)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nasname", length = 128, nullable = false)
    private String nasName;

    @Column(name = "shortname", length = 32)
    private String shortName;

    @Column(length = 30)
    private String type;

    @Column(length = 5)
    private Integer ports;

    @Column(length = 60, nullable = false)
    private String secret;

    @Column(length = 64)
    private String server;

    @Column(length = 50)
    private String community;

    @Column(length = 200)
    private String description;

}
