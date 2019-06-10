package software.netcore.radman.buisness.service.user.system.converter;

import org.springframework.core.convert.converter.Converter;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.data.internal.entity.AuthProvider;

/**
 * @since v. 1.0.0
 */
public class AuthProviderToDtoConverter implements Converter<AuthProvider, AuthProviderDto> {

    @Override
    public AuthProviderDto convert(AuthProvider source) {
        switch (source) {
            case LOCAL:
                return AuthProviderDto.LOCAL;
            case LDAP:
                return AuthProviderDto.LDAP;
            default:
                throw new IllegalStateException("Unknown auth provider '" + source + "'!");
        }
    }

}
