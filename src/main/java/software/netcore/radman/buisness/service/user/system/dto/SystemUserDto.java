package software.netcore.radman.buisness.service.user.system.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import software.netcore.radman.buisness.validation.constrain.Cheap;
import software.netcore.radman.buisness.validation.constrain.Expensive;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@GroupSequence(value = {Cheap.class, Expensive.class, SystemUserDto.class})
public class SystemUserDto {

    private Long id;

    @NotEmpty(message = "Username is required", groups = Cheap.class)
    @Pattern(regexp = "^[a-zA-Z0-9._\\-@]{3,64}$",
            message = "Username can consist of letters, numbers, any of the \\\"._-@\\\" characters, " +
                    "and its length has to be within 3 and 64 characters", groups = Expensive.class)
    private String username;

    @NotEmpty(message = "Password is required", groups = Cheap.class)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\!@#\\$%\\^&\\*\\(\\)\\-_\\=\\+\\.\\:;\\?]{8,64}$",
            message = "Password requires one lowercase letter, uppercase letter and number. " +
                    "Spaces, tabs nor unicode characters are not allowed. " +
                    "Its length has to be within 8 to 64 characters", groups = Expensive.class)
    private String password;

    private int passwordLength;

    @NotNull(message = "Role is required")
    private Role role;

    private Long lastLoginTime;

    public void setPassword(String password) {
        this.password = password;
        passwordLength = password.length();
    }

    @Override
    public String toString() {
        return "SystemUserDto{" +
                "id=" + id +
                ", username='" + username + "'" +
                ", password='" + passwordLength + " characters'" +
                ", passwordLength=" + passwordLength +
                ", role=" + role +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }

}
