package software.netcore.radman.buisness.service.attribute;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.buisness.service.dto.LoadingResult;
import software.netcore.radman.data.internal.entity.QRadCheckAttribute;
import software.netcore.radman.data.internal.entity.QRadReplyAttribute;
import software.netcore.radman.data.internal.entity.RadCheckAttribute;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;
import software.netcore.radman.data.internal.repo.RadCheckAttributeRepo;
import software.netcore.radman.data.internal.repo.RadReplyAttributeRepo;
import software.netcore.radman.data.radius.repo.RadCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupReplyRepo;
import software.netcore.radman.data.radius.repo.RadReplyRepo;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
public class AttributeService {

    private final RadCheckAttributeRepo checkAttributeRepo;
    private final RadReplyAttributeRepo replyAttributeRepo;

    private final RadCheckRepo radCheckRepo;
    private final RadReplyRepo radReplyRepo;
    private final RadGroupCheckRepo radGroupCheckRepo;
    private final RadGroupReplyRepo radGroupReplyRepo;

    private final ConversionService conversionService;

    public AttributeService(RadCheckAttributeRepo checkAttributeRepo,
                            RadReplyAttributeRepo replyAttributeRepo,
                            RadCheckRepo radCheckRepo,
                            RadReplyRepo radReplyRepo,
                            RadGroupCheckRepo radGroupCheckRepo,
                            RadGroupReplyRepo radGroupReplyRepo,
                            ConversionService conversionService) {
        this.checkAttributeRepo = checkAttributeRepo;
        this.replyAttributeRepo = replyAttributeRepo;
        this.radCheckRepo = radCheckRepo;
        this.radReplyRepo = radReplyRepo;
        this.radGroupCheckRepo = radGroupCheckRepo;
        this.radGroupReplyRepo = radGroupReplyRepo;
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


    public LoadingResult loadAuthorizationAttributesFromRadiusDB() {
        Set<String> radReplyAttr = radReplyRepo.getAttributes();
        Set<String> radGroupReplyAttr = radGroupReplyRepo.getAttributes();

        Set<String> attributes = new HashSet<>();
        attributes.addAll(radReplyAttr);
        attributes.addAll(radGroupReplyAttr);

        LoadingResult result = new LoadingResult();
        attributes.forEach(attribute -> {
            try {
                if (!replyAttributeRepo.exists(QRadReplyAttribute.radReplyAttribute.name.like(attribute))) {
                    RadReplyAttribute radReplyAttribute = new RadReplyAttribute();
                    radReplyAttribute.setSensitiveData(false);
                    radReplyAttribute.setName(attribute);
                    replyAttributeRepo.save(radReplyAttribute);
                    result.incrementLoaded();
                } else {
                    result.incrementDuplicate();
                }
            } catch (Exception ignored) {
                result.incrementErrored();
            }
        });
        return result;
    }

    public LoadingResult loadAuthenticationAttributesFromRadiusDB() {
        Set<String> radCheckAttr = radCheckRepo.getAttributes();
        Set<String> radGroupCheckAttr = radGroupCheckRepo.getAttributes();

        Set<String> attributes = new HashSet<>();
        attributes.addAll(radCheckAttr);
        attributes.addAll(radGroupCheckAttr);

        LoadingResult result = new LoadingResult();
        attributes.forEach(attribute -> {
            try {
                if (!checkAttributeRepo.exists(QRadCheckAttribute.radCheckAttribute.name.like(attribute))) {
                    RadCheckAttribute radCheckAttribute = new RadCheckAttribute();
                    radCheckAttribute.setSensitiveData(false);
                    radCheckAttribute.setName(attribute);
                    checkAttributeRepo.save(radCheckAttribute);
                    result.incrementLoaded();
                } else {
                    result.incrementDuplicate();
                }
            } catch (Exception ignored) {
                result.incrementErrored();
            }
        });
        return result;
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
