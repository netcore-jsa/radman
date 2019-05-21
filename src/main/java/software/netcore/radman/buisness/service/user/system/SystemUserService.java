package software.netcore.radman.buisness.service.user.system;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.internal.repo.SystemUserRepo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SystemUserService {

    private final SystemUserRepo systemUserRepo;
    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;

    public SystemUserDto createSystemUser(@NonNull SystemUserDto systemUserDto) {
        SystemUser systemUser = conversionService.convert(systemUserDto, SystemUser.class);
        systemUser.setPasswordLength(systemUser.getPassword().length());
        systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        systemUser = systemUserRepo.save(systemUser);
        return conversionService.convert(systemUser, SystemUserDto.class);
    }

    public SystemUserDto updateSystemUser(@NonNull SystemUserDto systemUserDto) {
        SystemUser systemUser = conversionService.convert(systemUserDto, SystemUser.class);
        systemUser = systemUserRepo.save(systemUser);
        return conversionService.convert(systemUser, SystemUserDto.class);
    }

    public long countSystemUsers() {
        return systemUserRepo.countSystemUsers();
    }

    public Page<SystemUserDto> pageSystemUsers(Pageable pageable) {
        Page<SystemUser> page = systemUserRepo.pageSystemUsers(pageable);
        List<SystemUserDto> userDtos = page.stream()
                .map(user -> conversionService.convert(user, SystemUserDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }

    public void deleteSystemUser(@NonNull SystemUserDto user) {
        systemUserRepo.deleteById(user.getId());
    }

}
