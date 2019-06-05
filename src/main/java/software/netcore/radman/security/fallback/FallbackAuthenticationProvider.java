package software.netcore.radman.security.fallback;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import software.netcore.radman.security.Constants;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class FallbackAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(authentication.getPrincipal()));
        if (Objects.nonNull(userDetails)) {
            if (Objects.equals(userDetails.getUsername(), authentication.getPrincipal())
                    && Objects.equals(userDetails.getPassword(), authentication.getCredentials())) {
                return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                        userDetails.getAuthorities());
            }
        }
        throw new UsernameNotFoundException(Constants.AUTHENTICATION_FAILURE_MESSAGE);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
