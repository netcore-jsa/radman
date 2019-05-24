package software.netcore.radman.buisness.service.attribute.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class AttributeDto {

    private Long id;

    @NotEmpty(message = "Name is required")
    @Length(min = 2, max = 64, message = "Attribute name length cannot be less than 2 and more than 64 characters")
    private String name;

    @NotNull
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
