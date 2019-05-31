package software.netcore.radman.buisness.service.auth.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.data.radius.entity.RadGroupReply;

/**
 * @since v. 1.0.0
 */
public class DtoToRadGroupReplyConverter implements DtoConverter<AuthorizationDto, RadGroupReply> {

    @Override
    public RadGroupReply convert(AuthorizationDto source) {
        RadGroupReply target = new RadGroupReply();
        target.setId(source.getId());
        target.setGroupName(source.getName());
        target.setAttribute(source.getAttribute());
        target.setOp(source.getOp().getValue());
        target.setValue(source.getValue());
        return target;
    }

}
