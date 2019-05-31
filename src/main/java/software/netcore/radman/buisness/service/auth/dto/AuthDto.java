package software.netcore.radman.buisness.service.auth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @since v. 1.0.0
 */
@Setter
@Getter
public abstract class AuthDto {

    private Integer id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotNull(message = "Type is required")
    private AuthTarget authTarget;

    @NotEmpty(message = "Attribute is required")
    private String attribute;

    @NotNull(message = "Operation is required")
    private RadiusOp op;

    @NotEmpty(message = "Value is required")
    private String value;

}
