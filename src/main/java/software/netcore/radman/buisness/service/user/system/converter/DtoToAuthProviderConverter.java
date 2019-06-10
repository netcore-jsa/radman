package software.netcore.radman.buisness.service.user.system.converter;

import org.springframework.core.convert.converter.Converter;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.data.internal.entity.AuthProvider;

/**
 * @since v. 1.0.0
 */
public class DtoToAuthProviderConverter implements Converter<AuthProviderDto, AuthProvider> {

    @Override
    public AuthProvider convert(AuthProviderDto source) {
        switch (source) {
            case LDAP:
                return AuthProvider.LDAP;
            case LOCAL:
                return AuthProvider.LOCAL;
            default:
                throw new IllegalStateException("Unknown auth provider '" + source + "'!");
        }
    }

}
