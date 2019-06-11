package software.netcore.radman.buisness.service.user.radius.converter;

import org.springframework.core.convert.converter.Converter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserToGroupDto;
import software.netcore.radman.data.radius.entity.RadUserGroup;

/**
 * @since v. 1.0.0
 */
public class RadUserGroupToDtoConverter implements Converter<RadUserGroup, RadiusUserToGroupDto> {

    @Override
    public RadiusUserToGroupDto convert(RadUserGroup source) {
        RadiusUserToGroupDto target = new RadiusUserToGroupDto();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setGroupName(source.getGroupName());
        return target;
    }

}
