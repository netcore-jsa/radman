package software.netcore.radman.buisness.service.nas.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class NasDto {

    private Integer id;
    private String nasName;
    private String shortName;
    private String type;
    private Integer ports;
    private String secret;
    private String server;
    private String community;
    private String description;

}
