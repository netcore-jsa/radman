package software.netcore.radman.ui.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;

/**
 * @since v. 1.0.0
 */
public class AttributeDtoToNameConverter<T extends AttributeDto> implements Converter<T, String> {

    private T attributeDto;

    @Override
    public Result<String> convertToModel(T value, ValueContext context) {
        if (value == null) {
            return Result.ok(null);
        }
        attributeDto = value;
        return Result.ok(value.getName());
    }

    @Override
    public T convertToPresentation(String value, ValueContext context) {
        if (value != null) {
            attributeDto.setName(value);
        }
        return attributeDto;
    }

}
