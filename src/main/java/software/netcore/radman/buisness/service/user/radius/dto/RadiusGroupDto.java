package software.netcore.radman.buisness.service.user.radius.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import software.netcore.radman.buisness.validation.constrain.Cheap;
import software.netcore.radman.buisness.validation.constrain.Expensive;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@GroupSequence(value = {Cheap.class, Expensive.class, RadiusGroupDto.class})
public class RadiusGroupDto {

    private Long id;

    @NotEmpty(message = "Name is required", groups = Cheap.class)
    @Length(min = 2, max = 64, message = "Name length " +
            "cannot be less than 2 and more than 64 characters", groups = Expensive.class)
    private String name;

    private String description;

}
