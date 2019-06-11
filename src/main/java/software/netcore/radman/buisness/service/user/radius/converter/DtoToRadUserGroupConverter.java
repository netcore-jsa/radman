package software.netcore.radman.buisness.service.user.radius.converter;

import org.springframework.core.convert.converter.Converter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserToGroupDto;
import software.netcore.radman.data.radius.entity.RadUserGroup;

/**
 * @since v. 1.0.0
 */
public class DtoToRadUserGroupConverter implements Converter<RadiusUserToGroupDto, RadUserGroup> {

    @Override
    public RadUserGroup convert(RadiusUserToGroupDto source) {
        RadUserGroup target = new RadUserGroup();
        target.setId(source.getId());
        target.setUsername(source.getUsername());
        target.setGroupName(source.getGroupName());
        return target;
    }

}
