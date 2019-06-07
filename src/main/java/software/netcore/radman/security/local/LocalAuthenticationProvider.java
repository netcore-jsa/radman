package software.netcore.radman.security.local;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.AuthenticationRefusedException;
import software.netcore.radman.security.Constants;
import software.netcore.radman.security.RoleAuthority;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class LocalAuthenticationProvider implements AuthenticationProvider {

    private final SystemUserRepo systemUserRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = String.valueOf(authentication.getPrincipal());
        SystemUser systemUser = systemUserRepo.findByUsername(username);
        if (Objects.isNull(systemUser)) {
            throw new AuthenticationRefusedException(Constants.AUTHENTICATION_FAILURE_MESSAGE);
        }
        if (systemUser.getAuthProvider() == AuthProvider.LOCAL) {
            if (passwordEncoder.matches(String.valueOf(authentication.getCredentials()), systemUser.getPassword())) {
                return new UsernamePasswordAuthenticationToken(username, systemUser.getPassword(),
                        RoleAuthority.asCollection(new RoleAuthority(systemUser.getRole())));
            }
        }
        throw new UsernameNotFoundException(Constants.AUTHENTICATION_FAILURE_MESSAGE);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
