package software.netcore.radman.buisness.service.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AuthTarget {

    RADIUS_USER("user"),
    RADIUS_GROUP("group");

    private final String value;

    public static AuthTarget fromValue(String value) {
        if (Objects.equals(value, RADIUS_USER.value)) {
            return RADIUS_USER;
        }
        if (Objects.equals(value, RADIUS_GROUP.value)) {
            return RADIUS_GROUP;
        }
        throw new IllegalStateException("No such enum with value '" + value + "'!");
    }

}
