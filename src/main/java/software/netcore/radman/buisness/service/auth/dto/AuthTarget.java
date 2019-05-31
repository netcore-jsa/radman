package software.netcore.radman.buisness.service.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since v. 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum AuthTarget {

    RADIUS_USER("user"),
    RADIUS_GROUP("group");

    private final String value;

}
