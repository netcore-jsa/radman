package software.netcore.radman.buisness.service.user.system.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.Role;
import software.netcore.radman.data.internal.entity.SystemUser;

/**
 * @since v. 1.0.0
 */
public class SystemUserToDtoConverter implements DtoConverter<SystemUser, SystemUserDto> {

    @Override
    public SystemUserDto convert(SystemUser source) {
        SystemUserDto target = new SystemUserDto();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        if (source.getAuthProvider() == AuthProvider.LOCAL) {
            target.setPassword(source.getPassword());
            target.setPasswordLength(source.getPasswordLength());
        }
        target.setLastLoginTime(source.getLastLoginTime());
        target.setRole(source.getRole() == Role.ADMIN ?
                software.netcore.radman.buisness.service.user.system.dto.Role.ADMIN :
                software.netcore.radman.buisness.service.user.system.dto.Role.READ_ONLY);
        target.setAuthProvider(source.getAuthProvider() == AuthProvider.LOCAL ?
                software.netcore.radman.buisness.service.user.system.dto.AuthProvider.LOCAL :
                software.netcore.radman.buisness.service.user.system.dto.AuthProvider.LDAP);
        return target;
    }

}
