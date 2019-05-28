package software.netcore.radman.data.radius.entity;

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
@Table(name = "radcheck")
public class RadCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "UNSIGNED INT(11)")
    private Integer id;

    @Column(nullable = false, length = 64)
    private String username; // name column : 1st

    @Column(nullable = false, length = 64)
    private String attribute; // correlates to rad check attribute name

    @Column(columnDefinition = "CHAR(2)", nullable = false)
    private String op;

    @Column(length = 253, nullable = false)
    private String value;

}
