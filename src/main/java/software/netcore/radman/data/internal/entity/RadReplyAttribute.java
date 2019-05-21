package software.netcore.radman.data.internal.entity;

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
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "radreply_attribute",
        uniqueConstraints = @UniqueConstraint(name = "uk_radreply_attribute_name", columnNames = {"name"}))
public class RadReplyAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "sensitive_data", nullable = false)
    private boolean sensitiveData;

}
