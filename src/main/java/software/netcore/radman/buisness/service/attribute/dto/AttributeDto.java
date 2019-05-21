package software.netcore.radman.buisness.service.attribute.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class AttributeDto {

    private Long id;
    private String name;
    private String description;
    private boolean sensitiveData;

}
