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
@Table(name = "radreply_attribute",
        uniqueConstraints = @UniqueConstraint(name = "uk_radreply_attribute_name", columnNames = {"name"}))
public class RadReplyAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean sensitive;

}
