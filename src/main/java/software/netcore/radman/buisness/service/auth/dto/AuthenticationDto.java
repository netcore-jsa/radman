package software.netcore.radman.buisness.service.auth.dto;

import java.util.List;
import java.util.Map;

/**
 * @since v. 1.0.0
 */
public class AuthenticationDto extends AuthDto {

    public AuthenticationDto(Map<String, String> columnsSpec,
                             List<Map<String, String>> data) {
        super(columnsSpec, data);
    }

}
