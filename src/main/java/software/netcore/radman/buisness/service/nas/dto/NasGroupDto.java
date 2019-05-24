package software.netcore.radman.buisness.service.nas.dto;

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
@GroupSequence(value = {Cheap.class, Expensive.class, NasGroupDto.class})
public class NasGroupDto {

    private Integer id;

    @NotEmpty(message = "Group name is required", groups = Cheap.class)
    @Length(min = 1, max = 64, message = "Group name length can be maximally 64 characters", groups = Expensive.class)
    private String groupName;

    @NotEmpty(message = "IP address is required", groups = Cheap.class)
    @Length(min = 1, max = 15, message = "IP address length can be maximally 15 characters", groups = Expensive.class)
    private String nasIpAddress;

    @Length(max = 15, message = "Port ID length can be maximally 15 characters")
    private String nasPortId;

    @Override
    public String toString() {
        return "NasGroupDto{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", nasIpAddress='" + nasIpAddress + '\'' +
                ", nasPortId='" + nasPortId + '\'' +
                '}';
    }

}
