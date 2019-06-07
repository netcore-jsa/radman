package software.netcore.radman.security;

import org.springframework.security.authentication.AccountStatusException;

/**
 * @since v. 1.0.0
 */
public class AuthenticationRefusedException extends AccountStatusException {

    public AuthenticationRefusedException(String msg) {
        super(msg);
    }

}
