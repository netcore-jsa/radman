package software.netcore.radman.security;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import software.netcore.radman.data.internal.entity.Role;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "role")
public class RoleAuthority implements GrantedAuthority {

    private final Role role;

    @Override
    public String getAuthority() {
        return role.toString();
    }

    public static Collection<? extends GrantedAuthority> asCollection(RoleAuthority... authority) {
        if (authority == null) {
            return null;
        }
        Set<RoleAuthority> authorities = new HashSet<>(authority.length);
        authorities.addAll(Arrays.asList(authority));
        return authorities;
    }

}
