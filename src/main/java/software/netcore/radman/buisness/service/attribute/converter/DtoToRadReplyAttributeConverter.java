package software.netcore.radman.buisness.service.attribute.converter;

import software.netcore.radman.buisness.service.attribute.dto.AuthorizationDto;
import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;

/**
 * @since v. 1.0.0
 */
public class DtoToRadReplyAttributeConverter implements DtoConverter<AuthorizationDto, RadReplyAttribute> {

    @Override
    public RadReplyAttribute convert(AuthorizationDto source) {
        RadReplyAttribute target = new RadReplyAttribute();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setSensitive(source.isSensitive());
        return target;
    }

}
