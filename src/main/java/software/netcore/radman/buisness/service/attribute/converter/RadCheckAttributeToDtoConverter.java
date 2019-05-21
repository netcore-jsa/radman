package software.netcore.radman.buisness.service.attribute.converter;

import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.data.internal.entity.RadCheckAttribute;

/**
 * @since v. 1.0.0
 */
public class RadCheckAttributeToDtoConverter implements DtoConverter<RadCheckAttribute, AuthenticationAttributeDto> {

    @Override
    public AuthenticationAttributeDto convert(RadCheckAttribute source) {
        AuthenticationAttributeDto target = new AuthenticationAttributeDto();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setSensitiveData(source.isSensitiveData());
        return target;
    }

}
