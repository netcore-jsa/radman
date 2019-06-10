package software.netcore.radman.buisness.service.user.system.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.Role;
import software.netcore.radman.data.internal.entity.SystemUser;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class DtoToSystemUserConverter implements DtoConverter<SystemUserDto, SystemUser> {

    private final ConversionService conversionService;

    @Override
    public SystemUser convert(SystemUserDto source) {
        SystemUser target = new SystemUser();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        if (source.getAuthProvider() == AuthProviderDto.LOCAL) {
            target.setPassword(source.getPassword());
            target.setPasswordLength(source.getPasswordLength());
        }
        target.setLastLoginTime(source.getLastLoginTime());
        target.setRole(conversionService.convert(source.getRole(), Role.class));
        target.setAuthProvider(conversionService.convert(source.getAuthProvider(), AuthProvider.class));
        return target;
    }

}
