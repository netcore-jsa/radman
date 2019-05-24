package software.netcore.radman.buisness.service.nas.dto;

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
public class NasGroupDto {

    private Integer id;

    @NotEmpty(message = "Group name is required")
    @Length(max = 64, message = "Group name length can be maximally 64 characters")
    private String groupName;

    @NotEmpty(message = "IP address is required")
    @Length(max = 15, message = "IP address length can be maximally 15 characters")
    private String nasIpAddress;

    @Length(max = 15, message = "Port ID length can be maximally 15 characters")
    private String nasPortId;

}
