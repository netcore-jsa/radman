package software.netcore.radman.data.internal.entity;

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
@Table(name = "radius_group",
        uniqueConstraints = @UniqueConstraint(name = "uk_radius_group_name", columnNames = {"name"}))
public class RadiusGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(length = 64, nullable = false, unique = true)
    private String name;

    @Column
    private String description;

}
