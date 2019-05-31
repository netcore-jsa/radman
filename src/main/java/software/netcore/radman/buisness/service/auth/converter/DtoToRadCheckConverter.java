package software.netcore.radman.buisness.service.auth.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.data.radius.entity.RadCheck;

/**
 * @since v. 1.0.0
 */
public class DtoToRadCheckConverter implements DtoConverter<AuthenticationDto, RadCheck> {

    @Override
    public RadCheck convert(AuthenticationDto source) {
        RadCheck target = new RadCheck();
        target.setId(source.getId());
        target.setUsername(source.getName());
        target.setAttribute(source.getAttribute());
        target.setOp(source.getOp().getValue());
        target.setValue(source.getValue());
        return target;
    }

}
