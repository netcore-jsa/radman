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
@Table(name = "system_user",
        uniqueConstraints = @UniqueConstraint(name = "uk_system_user_username", columnNames = {"username"}))
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_length", nullable = false)
    private int passwordLength;

    @Column(length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "last_login_time")
    private Long lastLoginTime;

}
