package software.netcore.radman.buisness.service.user.radius;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.data.internal.entity.RadiusGroup;
import software.netcore.radman.data.internal.entity.RadiusUser;
import software.netcore.radman.data.internal.repo.RadiusGroupRepo;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;
import software.netcore.radman.data.radius.repo.RadUserGroupRepo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class RadiusUserService {

    private final RadiusUserRepo radiusUserRepo;
    private final RadiusGroupRepo radiusGroupRepo;
    private final RadUserGroupRepo radUserGroupRepo;
    private final ConversionService conversionService;

    public RadiusUserDto createRadiusUser(RadiusUserDto radiusUserDto) {
        assert radiusUserDto.getId() == null;
        RadiusUser radiusUser = conversionService.convert(radiusUserDto, RadiusUser.class);
        radiusUser = radiusUserRepo.save(radiusUser);
        return conversionService.convert(radiusUser, RadiusUserDto.class);
    }

    public RadiusUserDto updateRadiusUser(RadiusUserDto radiusUserDto) {
        assert radiusUserDto.getId() != null;
        RadiusUser radiusUser = conversionService.convert(radiusUserDto, RadiusUser.class);
        radiusUser = radiusUserRepo.save(radiusUser);
        return conversionService.convert(radiusUser, RadiusUserDto.class);
    }

    public void deleteRadiusUser(RadiusUserDto radiusUserDto) {
        radiusUserRepo.deleteById(radiusUserDto.getId());
    }

    public long countRadiusUsers() {
        return radiusUserRepo.count();
    }

    public Page<RadiusUserDto> pageRadiusUsers(Pageable pageable) {
        Page<RadiusUser> page = radiusUserRepo.pageRadiusUsers(pageable);
        List<RadiusUserDto> userDtos = page.stream()
                .map(user -> conversionService.convert(user, RadiusUserDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }

    public RadiusGroupDto createRadiusUsersGroup(RadiusGroupDto radiusGroupDto) {
        assert radiusGroupDto.getId() == null;
        RadiusGroup radiusGroup = conversionService.convert(radiusGroupDto, RadiusGroup.class);
        radiusGroup = radiusGroupRepo.save(radiusGroup);
        return conversionService.convert(radiusGroup, RadiusGroupDto.class);
    }

    public RadiusGroupDto updateRadiusUsersGroup(RadiusGroupDto radiusGroupDto) {
        assert radiusGroupDto.getId() != null;
        RadiusGroup radiusGroup = conversionService.convert(radiusGroupDto, RadiusGroup.class);
        radiusGroup = radiusGroupRepo.save(radiusGroup);
        return conversionService.convert(radiusGroup, RadiusGroupDto.class);
    }

    public void deleteRadiusUsersGroup(RadiusGroupDto radiusGroup) {
        radiusGroupRepo.deleteById(radiusGroup.getId());
    }

    public long countRadiusUsersGroup() {
        return radiusGroupRepo.count();
    }

    public Page<RadiusGroupDto> pageRadiusUsersGroup(Pageable pageable) {
        Page<RadiusGroup> page = radiusGroupRepo.pageRadiusUsersGroups(pageable);
        List<RadiusGroupDto> userDtos = page.stream()
                .map(user -> conversionService.convert(user, RadiusGroupDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }

}
