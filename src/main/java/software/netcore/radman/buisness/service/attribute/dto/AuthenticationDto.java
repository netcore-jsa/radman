package software.netcore.radman.buisness.service.attribute.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class AuthenticationDto {

    private Long id;
    private String name;
    private String description;
    private boolean sensitive;

}
