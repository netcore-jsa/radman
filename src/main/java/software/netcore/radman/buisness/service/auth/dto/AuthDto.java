package software.netcore.radman.buisness.service.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
abstract class AuthDto {

    private final Map<String, String> columnsSpec;
    private final List<Map<String, String>> data;

}
