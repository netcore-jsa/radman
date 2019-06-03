package software.netcore.radman.ui.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;

/**
 * @since v. 1.0.0
 */
public class RadiusGroupDtoToNameConverter implements Converter<RadiusGroupDto, String> {

    private RadiusGroupDto radiusGroupDto;

    @Override
    public Result<String> convertToModel(RadiusGroupDto value, ValueContext context) {
        if (value == null) {
            return Result.ok("");
        }
        radiusGroupDto = value;
        return Result.ok(value.getName());
    }

    @Override
    public RadiusGroupDto convertToPresentation(String value, ValueContext context) {
        if (value != null) {
            radiusGroupDto.setName(value);
        }
        return radiusGroupDto;
    }

}
