package software.netcore.radman.data.radius.entity;

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
@NoArgsConstructor
@Table(name = "radhuntgroup")
public class RadHuntGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(columnDefinition = "UNSIGNED INT(11)")
    private Integer id;

    @Column(name = "groupname", nullable = false, length = 64)
    private String groupName;

    @Column(name = "nasipaddress", nullable = false, length = 15)
    private String nasIpAddress;

    @Column(name = "nasportid", length = 15)
    private String nasPortId;

}
