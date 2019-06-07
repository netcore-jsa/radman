package software.netcore.radman.ui.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;

/**
 * @since v. 1.0.0
 */
public class RadiusUserDtoToNameConverter implements Converter<RadiusUserDto, String> {

    private RadiusUserDto radiusUserDto;

    @Override
    public Result<String> convertToModel(RadiusUserDto value, ValueContext context) {
        if (value == null) {
            return Result.ok("");
        }
        radiusUserDto = value;
        return Result.ok(value.getUsername());
    }

    @Override
    public RadiusUserDto convertToPresentation(String value, ValueContext context) {
        if (value != null) {
            radiusUserDto.setUsername(value);
        }
        return radiusUserDto;
    }

}
