package software.netcore.radman.buisness.service.user.system.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
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
        target.setPassword(source.getPassword());
        target.setPasswordLength(source.getPasswordLength());
        target.setLastLoginTime(source.getLastLoginTime());
        target.setRole(source.getRole() == Role.ADMIN ?
                software.netcore.radman.buisness.service.user.system.dto.Role.ADMIN :
                software.netcore.radman.buisness.service.user.system.dto.Role.READ_ONLY);
        return target;
    }

}
