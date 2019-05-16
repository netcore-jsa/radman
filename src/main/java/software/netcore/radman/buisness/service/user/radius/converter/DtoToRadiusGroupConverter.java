package software.netcore.radman.buisness.service.user.radius.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.data.internal.entity.RadiusGroup;

/**
 * @since v. 1.0.0
 */
public class DtoToRadiusGroupConverter implements DtoConverter<RadiusGroupDto, RadiusGroup> {

    @Override
    public RadiusGroup convert(RadiusGroupDto source) {
        RadiusGroup target = new RadiusGroup();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        return target;
    }

}
