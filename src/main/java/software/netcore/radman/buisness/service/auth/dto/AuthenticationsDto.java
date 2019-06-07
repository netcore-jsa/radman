package software.netcore.radman.buisness.service.auth.dto;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @since v. 1.0.0
 */
@NoArgsConstructor
public class AuthenticationsDto extends AuthsDto {

    public AuthenticationsDto(Map<String, String> columnsSpec,
                              List<Map<String, String>> data) {
        super(columnsSpec, data);
    }

}
