package software.netcore.radman.ui.converter;


import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import lombok.NoArgsConstructor;

/**
 * @since v. 1.0.0
 */
@NoArgsConstructor
public class DoubleToIntegerConverter implements Converter<Double, Integer> {

    private String errorMessage = "Cannot convert value from presentation to model";

    public DoubleToIntegerConverter(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public Result<Integer> convertToModel(Double value, ValueContext context) {
        try {
            if (value == null) {
                return Result.ok(null);
            }
            return Result.ok(value.intValue());
        } catch (Exception ex) {
            return Result.error(errorMessage);
        }
    }

    @Override
    public Double convertToPresentation(Integer value, ValueContext context) {
        if (value == null) {
            return null;
        }
        return Double.valueOf(value);
    }

}
