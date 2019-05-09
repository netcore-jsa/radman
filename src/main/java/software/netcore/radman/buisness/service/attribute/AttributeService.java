package software.netcore.radman.buisness.service.attribute;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationDto;
import software.netcore.radman.data.internal.repo.RadCheckAttributeRepo;
import software.netcore.radman.data.internal.repo.RadReplyAttributeRepo;

import java.util.Collections;

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

    public int countAuthenticationAttributeRecords() {
        return 0;
    }

    public Page<AuthenticationDto> pageAuthenticationAttributeRecords(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList());
    }

    public int countAuthorizationAttributeRecords() {
        return 0;
    }

    public Page<AuthorizationDto> pageAuthorizationAttributeRecords(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList());
    }

}
