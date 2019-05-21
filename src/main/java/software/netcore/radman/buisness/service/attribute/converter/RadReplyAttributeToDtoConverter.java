package software.netcore.radman.buisness.service.attribute.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;

/**
 * @since v. 1.0.0
 */
public class RadReplyAttributeToDtoConverter implements DtoConverter<RadReplyAttribute, AuthorizationAttributeDto> {

    @Override
    public AuthorizationAttributeDto convert(RadReplyAttribute source) {
        AuthorizationAttributeDto target = new AuthorizationAttributeDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setSensitiveData(source.isSensitiveData());
        return target;
    }

}
