package software.netcore.radman.buisness.service.user.system.converter;

import org.springframework.core.convert.converter.Converter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.data.internal.entity.Role;

/**
 * @since v. 1.0.0
 */
public class DtoToRoleConverter implements Converter<RoleDto, Role> {

    @Override
    public Role convert(RoleDto source) {
        switch (source) {
            case ADMIN:
                return Role.ADMIN;
            case READ_ONLY:
                return Role.READ_ONLY;
            default:
                throw new IllegalStateException("Unknown role '" + source + "'!");
        }
    }

}
