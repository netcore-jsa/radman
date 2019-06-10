package software.netcore.radman.buisness.service.user.system.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.SystemUser;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class SystemUserToDtoConverter implements DtoConverter<SystemUser, SystemUserDto> {

    private final ConversionService conversionService;

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
        target.setRole(conversionService.convert(source.getRole(), RoleDto.class));
        target.setAuthProvider(conversionService.convert(source.getAuthProvider(), AuthProviderDto.class));
        return target;
    }

}
