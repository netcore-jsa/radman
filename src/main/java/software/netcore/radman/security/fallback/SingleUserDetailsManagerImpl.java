package software.netcore.radman.security.fallback;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import software.netcore.radman.security.Constants;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
public class SingleUserDetailsManagerImpl implements SingleUserDetailsManager {

    private UserDetails userDetails;

    @Override
    public void createUser(UserDetails user) {
        this.userDetails = user;
    }

    @Override
    public void deleteUser() {
        userDetails = null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Objects.nonNull(userDetails) && Objects.equals(userDetails.getUsername(), username)) {
            return userDetails;
        }
        throw new UsernameNotFoundException(Constants.AUTHENTICATION_FAILURE_MESSAGE);
    }

}
