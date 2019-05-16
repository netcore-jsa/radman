package software.netcore.radman.buisness.service.user.system;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.repo.SystemUserRepo;

import java.util.ArrayList;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class SystemUserService {

    private final SystemUserRepo systemUserRepo;
    private final ConversionService conversionService;

    public Page<SystemUserDto> pageSystemUsers(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>());
    }

    public long countSystemUsers() {
        return 0;
    }
}
