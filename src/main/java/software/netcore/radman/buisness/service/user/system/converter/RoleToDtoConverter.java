package software.netcore.radman.buisness.service.user.system.converter;

import org.springframework.core.convert.converter.Converter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.data.internal.entity.Role;

/**
 * @since v. 1.0.0
 */
public class RoleToDtoConverter implements Converter<Role, RoleDto> {

    @Override
    public RoleDto convert(Role source) {
        switch (source) {
            case ADMIN:
                return RoleDto.ADMIN;
            case READ_ONLY:
                return RoleDto.READ_ONLY;
            default:
                throw new IllegalStateException("Unknown role '" + source + "'!");
        }

    }

}
