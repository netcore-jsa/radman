package software.netcore.radman.ui.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;

public class NasGroupDtoToNameConverter implements Converter<NasGroupDto, String> {

    private NasGroupDto nasGroupDto;

    @Override
    public Result<String> convertToModel(NasGroupDto value, ValueContext context) {
        if (value == null) {
            return Result.ok("");
        }
        nasGroupDto = value;
        return Result.ok(value.getGroupName());
    }

    @Override
    public NasGroupDto convertToPresentation(String value, ValueContext context) {
        if (value != null) {
            nasGroupDto.setGroupName(value);
        }
        return nasGroupDto;
    }

}
