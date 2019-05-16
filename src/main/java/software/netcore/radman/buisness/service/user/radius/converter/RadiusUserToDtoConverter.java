package software.netcore.radman.buisness.service.user.radius.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.data.internal.entity.RadiusUser;

/**
 * @since v. 1.0.0
 */
public class RadiusUserToDtoConverter implements DtoConverter<RadiusUser, RadiusUserDto> {

    @Override
    public RadiusUserDto convert(RadiusUser source) {
        RadiusUserDto target = new RadiusUserDto();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setDescription(source.getDescription());
        return target;
    }

}
