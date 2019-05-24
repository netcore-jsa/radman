package software.netcore.radman.buisness.service.attribute.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@EqualsAndHashCode(of = "id")
@GroupSequence(value = {Cheap.class, Expensive.class, AttributeDto.class})
public abstract class AttributeDto {

    private Long id;

    @NotEmpty(message = "Name is required", groups = Cheap.class)
    @Length(min = 2, max = 64, message = "Attribute name length " +
            "cannot be less than 2 and more than 64 characters", groups = Expensive.class)
    private String name;

    private String description;

    private boolean sensitiveData;

    @Override
    public String toString() {
        return "AttributeDto{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", sensitiveData=" + sensitiveData +
                '}';
    }

}
