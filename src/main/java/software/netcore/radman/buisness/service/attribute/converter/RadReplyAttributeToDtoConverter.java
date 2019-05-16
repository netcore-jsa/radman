package software.netcore.radman.buisness.service.attribute.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;

/**
 * @since v. 1.0.0
 */
public class RadReplyAttributeToDtoConverter implements DtoConverter<RadReplyAttribute, AuthenticationAttributeDto> {

    @Override
    public AuthenticationAttributeDto convert(RadReplyAttribute source) {
        AuthenticationAttributeDto target = new AuthenticationAttributeDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setSensitive(source.isSensitive());
        return target;
    }

}
