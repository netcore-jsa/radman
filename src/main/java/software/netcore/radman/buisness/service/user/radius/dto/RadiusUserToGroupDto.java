package software.netcore.radman.buisness.service.user.radius.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @since v. 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
public class RadiusUserToGroupDto {

    private Integer id;

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Group name is required")
    private String groupName;

    private boolean userInRadman;

    private boolean groupInRadman;

}
