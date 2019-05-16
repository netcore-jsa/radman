package software.netcore.radman.buisness.service.user.system.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemUserDto {

    private Long id;
    private String username;
    private String password;
    private int passwordLength;
    private Role role;
    private Long lastLoginTime;

}
    