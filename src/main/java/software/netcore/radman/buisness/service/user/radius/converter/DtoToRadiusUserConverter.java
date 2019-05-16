package software.netcore.radman.buisness.service.user.radius.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.data.internal.entity.RadiusUser;

/**
 * @since v. 1.0.0
 */
public class DtoToRadiusUserConverter implements DtoConverter<RadiusUserDto, RadiusUser> {

    @Override
    public RadiusUser convert(RadiusUserDto source) {
        RadiusUser target = new RadiusUser();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setDescription(source.getDescription());
        return target;
    }

}
