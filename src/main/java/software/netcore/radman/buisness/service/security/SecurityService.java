package software.netcore.radman.buisness.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.Role;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.RoleAuthority;
import software.netcore.radman.security.fallback.SingleUserDetailsManager;

import java.util.Iterator;

/**
 * @since v. 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityService {

    private final SingleUserDetailsManager userDetailsManager;
    private final SystemUserRepo systemUserRepo;

    public RoleDto getLoggedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Iterator<? extends GrantedAuthority> iterator = authentication.getAuthorities().iterator();
        if (iterator.hasNext()) {
            GrantedAuthority authority = iterator.next();
            return RoleDto.valueOf(authority.getAuthority());
        }
        log.error("Logged in user has no role assigned! Returning '{}'", RoleDto.READ_ONLY);
        return RoleDto.READ_ONLY;
    }

    public void initiateFallbackUser() {
        if (systemUserRepo.countByRoleAndAuthProvider(Role.ADMIN, AuthProvider.LOCAL) == 0) {
            String username = generateUsername();
            String password = generatePassword();
            userDetailsManager.createUser(new User(username, password,
                    RoleAuthority.asCollection(new RoleAuthority(Role.ADMIN))));
            log.warn("No system user present. Please use this credentials to login - username = '{}'," +
                    " password = '{}'", username, password);
        }
    }

    private String generateUsername() {
        return RandomStringUtils.randomAlphanumeric(6, 7);
    }

    private String generatePassword() {
        return "P" + RandomStringUtils.randomAlphabetic(8, 10);
    }

}
