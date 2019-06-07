package software.netcore.radman.buisness.service.auth.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.data.radius.entity.RadReply;

/**
 * @since v. 1.0.0
 */
public class DtoToRadReplyConverter implements DtoConverter<AuthorizationDto, RadReply> {

    @Override
    public RadReply convert(AuthorizationDto source) {
        RadReply target = new RadReply();
        target.setId(source.getId());
        target.setUsername(source.getName());
        target.setAttribute(source.getAttribute());
        target.setOp(source.getOp().getValue());
        target.setValue(source.getValue());
        return target;
    }

}
