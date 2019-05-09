package software.netcore.radman.buisness.service.attribute.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationDto;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;

/**
 * @since v. 1.0.0
 */
public class RadReplyAttributeToDtoConverter implements DtoConverter<RadReplyAttribute, AuthenticationDto> {

    @Override
    public AuthenticationDto convert(RadReplyAttribute source) {
        AuthenticationDto target = new AuthenticationDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setSensitive(source.isSensitive());
        return target;
    }

}
