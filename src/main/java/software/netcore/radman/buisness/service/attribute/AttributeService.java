package software.netcore.radman.buisness.service.attribute;

import lombok.NonNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
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

    public long countAuthenticationAttributeRecords() {
        return checkAttributeRepo.count();
    }

    public Page<AuthenticationAttributeDto> pageAuthenticationAttributeRecords(Pageable pageable) {
        Page<RadCheckAttribute> page = checkAttributeRepo.pageCheckAttributes(pageable);
        List<AuthenticationAttributeDto> attributeDtos = page.stream()
                .map(attribute -> conversionService.convert(attribute, AuthenticationAttributeDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(attributeDtos, pageable, attributeDtos.size());
    }

    public long countAuthorizationAttributeRecords() {
        return replyAttributeRepo.count();
    }

    public Page<AuthorizationAttributeDto> pageAuthorizationAttributeRecords(Pageable pageable) {
        Page<RadReplyAttribute> page = replyAttributeRepo.pageReplyAttributes(pageable);
        List<AuthorizationAttributeDto> attributeDtos = page.stream()
                .map(attribute -> conversionService.convert(attribute, AuthorizationAttributeDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(attributeDtos, pageable, attributeDtos.size());
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

    public AuthorizationAttributeDto updateAuthorizationAttribute(@NonNull AuthorizationAttributeDto attributeDto) {
        RadReplyAttribute attribute = conversionService.convert(attributeDto, RadReplyAttribute.class);
        attribute = replyAttributeRepo.save(attribute);
        return conversionService.convert(attribute, AuthorizationAttributeDto.class);
    }

    public AuthenticationAttributeDto updateAuthenticationAttribute(@NotNull AuthenticationAttributeDto attributeDto) {
        RadCheckAttribute attribute = conversionService.convert(attributeDto, RadCheckAttribute.class);
        attribute = checkAttributeRepo.save(attribute);
        return conversionService.convert(attribute, AuthenticationAttributeDto.class);
    }

}
