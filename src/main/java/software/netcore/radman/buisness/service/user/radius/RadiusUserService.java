package software.netcore.radman.buisness.service.user.radius;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import software.netcore.radman.data.internal.repo.RadiusGroupRepo;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;
import software.netcore.radman.data.radius.repo.RadUserGroupRepo;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class RadiusUserService {

    private final RadiusUserRepo radiusUserRepo;
    private final RadiusGroupRepo radiusGroupRepo;
    private final RadUserGroupRepo radUserGroupRepo;
    private final ConversionService conversionService;

}
