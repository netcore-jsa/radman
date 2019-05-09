package software.netcore.radman.buisness.conversion;

import org.springframework.core.convert.converter.Converter;

/**
 * @param <S> the source type
 * @param <T> the target type
 * @since v. 1.0.0
 */
public interface DtoConverter<S, T> extends Converter<S, T> {
}
