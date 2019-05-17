package software.netcore.radman.buisness.service.user.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import software.netcore.radman.buisness.exception.DuplicityException;
import software.netcore.radman.buisness.exception.ValidationException;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.internal.repo.SystemUserRepo;

import java.util.List;
import java.util.Objects;
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

    public Page<SystemUserDto> pageSystemUsers(Pageable pageable) {
        Page<SystemUser> page = systemUserRepo.pageSystemUsers(pageable);
        List<SystemUserDto> userDtos = page.stream()
                .map(user -> conversionService.convert(user, SystemUserDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }

    public long countSystemUsers() {
        return systemUserRepo.countSystemUsers();
    }

    @Transactional(transactionManager = "txRadman")
    public void createSystemUser(SystemUserDto systemUserDto) throws DuplicityException, ValidationException {
//        try {
            SystemUser systemUser = conversionService.convert(systemUserDto, SystemUser.class);
            if (Objects.isNull(systemUser) || Objects.nonNull(systemUser.getId())) {
                throw new ValidationException("Cannot create system user. Request contained invalid data");
            }
            systemUser.setPasswordLength(systemUser.getPassword().length());
            systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
            systemUserRepo.save(systemUser);
//        } catch (DataIntegrityViolationException ex) {
//            throw new DuplicityException("User with same username already exist", ex);
//        }
    }

}
