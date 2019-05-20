package software.netcore.radman.buisness.service.user.system.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class SystemUserDto {

    private Long id;
    private String username;
    private String password;
    private int passwordLength;
    private Role role;
    private Long lastLoginTime;

}
