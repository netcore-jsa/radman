package software.netcore.radman.buisness.service.nas.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class NasGroupDto {

    private Integer id;
    private String groupName;
    private String nasIpAddress;
    private String nasPortId;

}
