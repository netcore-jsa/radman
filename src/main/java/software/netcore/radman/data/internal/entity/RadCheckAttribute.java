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
@Table(name = "radcheck_attribute",
        uniqueConstraints = @UniqueConstraint(name = "uk_radcheck_attribute_name", columnNames = {"name"}))
public class RadCheckAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false)
    private String name; // attribute column  name : 3rd-nth column

    @Column
    private String description;

    @Column(nullable = false)
    private boolean sensitive;

}
