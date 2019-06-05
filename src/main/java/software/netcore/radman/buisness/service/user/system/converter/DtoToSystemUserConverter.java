package software.netcore.radman.buisness.service.user.system.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.system.dto.AuthProvider;
import software.netcore.radman.buisness.service.user.system.dto.Role;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.entity.SystemUser;

/**
 * @since v. 1.0.0
 */
public class DtoToSystemUserConverter implements DtoConverter<SystemUserDto, SystemUser> {

    @Override
    public SystemUser convert(SystemUserDto source) {
        SystemUser target = new SystemUser();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        if (source.getAuthProvider() == AuthProvider.LOCAL) {
            target.setPassword(source.getPassword());
            target.setPasswordLength(source.getPasswordLength());
        }
        target.setLastLoginTime(source.getLastLoginTime());
        target.setRole(source.getRole() == Role.ADMIN ? software.netcore.radman.data.internal.entity.Role.ADMIN :
                software.netcore.radman.data.internal.entity.Role.READ_ONLY);
        target.setAuthProvider(source.getAuthProvider() == AuthProvider.LOCAL ?
                software.netcore.radman.data.internal.entity.AuthProvider.LOCAL
                : software.netcore.radman.data.internal.entity.AuthProvider.LDAP);
        return target;
    }

}
