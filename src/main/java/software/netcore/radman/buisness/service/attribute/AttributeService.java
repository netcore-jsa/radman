package software.netcore.radman.buisness.service.attribute;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.data.internal.entity.QRadCheckAttribute;
import software.netcore.radman.data.internal.entity.QRadReplyAttribute;
import software.netcore.radman.data.internal.entity.RadCheckAttribute;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;
import software.netcore.radman.data.internal.repo.RadCheckAttributeRepo;
import software.netcore.radman.data.internal.repo.RadReplyAttributeRepo;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
public class AttributeService {

    private final RadCheckAttributeRepo checkAttributeRepo;
    private final RadReplyAttributeRepo replyAttributeRepo;
    private final ConversionService conversionService;

    public AttributeService(RadCheckAttributeRepo checkAttributeRepo,
                            RadReplyAttributeRepo replyAttributeRepo,
                            ConversionService conversionService) {
        this.checkAttributeRepo = checkAttributeRepo;
        this.replyAttributeRepo = replyAttributeRepo;
        this.conversionService = conversionService;
    }

    public AuthenticationAttributeDto createAuthenticationAttribute(@NonNull AuthenticationAttributeDto attributeDto) {
        RadCheckAttribute attribute = conversionService.convert(attributeDto, RadCheckAttribute.class);
        attribute = checkAttributeRepo.save(attribute);
        return conversionService.convert(attribute, AuthenticationAttributeDto.class);
    }

    public AuthorizationAttributeDto createAuthorizationAttribute(@NonNull AuthorizationAttributeDto attributeDto) {
        RadReplyAttribute attribute = conversionService.convert(attributeDto, RadReplyAttribute.class);
        attribute = replyAttributeRepo.save(attribute);
        return conversionService.convert(attribute, AuthorizationAttributeDto.class);
    }

    public AuthenticationAttributeDto updateAuthenticationAttribute(@NotNull AuthenticationAttributeDto attributeDto) {
        RadCheckAttribute attribute = conversionService.convert(attributeDto, RadCheckAttribute.class);
        attribute = checkAttributeRepo.save(attribute);
        return conversionService.convert(attribute, AuthenticationAttributeDto.class);
    }

    public AuthorizationAttributeDto updateAuthorizationAttribute(@NonNull AuthorizationAttributeDto attributeDto) {
        RadReplyAttribute attribute = conversionService.convert(attributeDto, RadReplyAttribute.class);
        attribute = replyAttributeRepo.save(attribute);
        return conversionService.convert(attribute, AuthorizationAttributeDto.class);
    }

    public void deleteAuthenticationAttribute(@NonNull AuthenticationAttributeDto attributeDto) {
        checkAttributeRepo.deleteById(attributeDto.getId());
    }

    public void deleteAuthorizationAttribute(@NonNull AuthorizationAttributeDto attributeDto) {
        replyAttributeRepo.deleteById(attributeDto.getId());
    }

    public long countAuthenticationAttributeRecords(@NonNull AttributeFilter filter) {
        return checkAttributeRepo.count(buildAuthenticationAttributeSearchPredicate(filter));
    }

    public Page<AuthenticationAttributeDto> pageAuthenticationAttributeRecords(@NonNull AttributeFilter filter,
                                                                               @NonNull Pageable pageable) {
        Page<RadCheckAttribute> page = checkAttributeRepo.findAll(
                buildAuthenticationAttributeSearchPredicate(filter), pageable);
        List<AuthenticationAttributeDto> attributeDtos = page.stream()
                .map(attribute -> conversionService.convert(attribute, AuthenticationAttributeDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(attributeDtos, pageable, attributeDtos.size());
    }

    public long countAuthorizationAttributeRecords(@NonNull AttributeFilter filter) {
        return replyAttributeRepo.count(buildAuthorizationAttributeSearchPredicate(filter));
    }

    public Page<AuthorizationAttributeDto> pageAuthorizationAttributeRecords(@NonNull AttributeFilter filter,
                                                                             @NonNull Pageable pageable) {
        Page<RadReplyAttribute> page = replyAttributeRepo.findAll(
                buildAuthorizationAttributeSearchPredicate(filter), pageable);
        List<AuthorizationAttributeDto> attributeDtos = page.stream()
                .map(attribute -> conversionService.convert(attribute, AuthorizationAttributeDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(attributeDtos, pageable, attributeDtos.size());
    }

    private Predicate buildAuthenticationAttributeSearchPredicate(AttributeFilter filter) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(filter.getSearchText())) {
            if (filter.isSearchByName()) {
                booleanBuilder.or(QRadCheckAttribute.radCheckAttribute.name.contains(filter.getSearchText()));
            }
            if (filter.isSearchByDescription()) {
                booleanBuilder.or(QRadCheckAttribute.radCheckAttribute.description.contains(filter.getSearchText()));
            }
        }
        return booleanBuilder;
    }

    private Predicate buildAuthorizationAttributeSearchPredicate(AttributeFilter filter) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(filter.getSearchText())) {
            if (filter.isSearchByName()) {
                booleanBuilder.or(QRadReplyAttribute.radReplyAttribute.name.contains(filter.getSearchText()));
            }
            if (filter.isSearchByDescription()) {
                booleanBuilder.or(QRadReplyAttribute.radReplyAttribute.description.contains(filter.getSearchText()));
            }
        }
        return booleanBuilder;
    }

}
