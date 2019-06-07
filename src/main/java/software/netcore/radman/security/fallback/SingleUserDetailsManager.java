package software.netcore.radman.security.fallback;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @since v. 1.0.0
 */
public interface SingleUserDetailsManager extends UserDetailsService {

    void createUser(UserDetails user);

    void deleteUser();

}
