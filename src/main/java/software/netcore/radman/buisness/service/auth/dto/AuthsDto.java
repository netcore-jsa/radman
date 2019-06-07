package software.netcore.radman.buisness.service.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public abstract class AuthsDto {

    private Map<String, String> columnsSpec;
    private List<Map<String, String>> data;

    AuthsDto(Map<String, String> columnsSpec,
             List<Map<String, String>> data) {
        this.columnsSpec = columnsSpec;
        this.data = data;
    }

}
