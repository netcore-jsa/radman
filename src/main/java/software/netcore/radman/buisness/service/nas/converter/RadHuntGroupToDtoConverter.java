package software.netcore.radman.buisness.service.nas.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.data.radius.entity.RadHuntGroup;

/**
 * @since v. 1.0.0
 */
public class RadHuntGroupToDtoConverter implements DtoConverter<RadHuntGroup, NasGroupDto> {

    @Override
    public NasGroupDto convert(RadHuntGroup source) {
        NasGroupDto target = new NasGroupDto();
        target.setId(source.getId());
        target.setGroupName(source.getGroupName());
        target.setNasIpAddress(source.getNasIpAddress());
        target.setNasPortId(source.getNasPortId());
        return target;
    }

}
