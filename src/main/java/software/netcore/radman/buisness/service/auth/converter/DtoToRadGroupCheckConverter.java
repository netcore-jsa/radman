package software.netcore.radman.buisness.service.auth.converter;

import software.netcore.radman.buisness.conversion.DtoConverter;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.data.radius.entity.RadGroupCheck;

/**
 * @since v. 1.0.0
 */
public class DtoToRadGroupCheckConverter implements DtoConverter<AuthenticationDto, RadGroupCheck> {

    @Override
    public RadGroupCheck convert(AuthenticationDto source) {
        RadGroupCheck target = new RadGroupCheck();
        target.setId(source.getId());
        target.setGroupName(source.getName());
        target.setAttribute(source.getAttribute());
        target.setOp(source.getOp().getValue());
        target.setValue(source.getValue());
        return target;
    }

}
