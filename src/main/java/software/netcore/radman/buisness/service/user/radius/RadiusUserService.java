package software.netcore.radman.buisness.service.user.radius;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupFilter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserFilter;
import software.netcore.radman.data.internal.entity.QRadiusGroup;
import software.netcore.radman.data.internal.entity.QRadiusUser;
import software.netcore.radman.data.internal.entity.RadiusGroup;
import software.netcore.radman.data.internal.entity.RadiusUser;
import software.netcore.radman.data.internal.repo.RadiusGroupRepo;
import software.netcore.radman.data.internal.repo.RadiusUserRepo;
import software.netcore.radman.data.radius.repo.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class RadiusUserService {

    private final RadiusUserRepo radiusUserRepo;
    private final RadiusGroupRepo radiusGroupRepo;
    private final RadUserGroupRepo radUserGroupRepo;

    private final RadCheckRepo radCheckRepo;
    private final RadReplyRepo radReplyRepo;
    private final RadGroupCheckRepo radGroupCheckRepo;
    private final RadGroupReplyRepo radGroupReplyRepo;

    private final ConversionService conversionService;

    public RadiusUserDto createRadiusUser(@NonNull RadiusUserDto radiusUserDto) {
        assert radiusUserDto.getId() == null;
        RadiusUser radiusUser = conversionService.convert(radiusUserDto, RadiusUser.class);
        radiusUser = radiusUserRepo.save(radiusUser);
        return conversionService.convert(radiusUser, RadiusUserDto.class);
    }

    public RadiusUserDto updateRadiusUser(@NonNull RadiusUserDto radiusUserDto) {
        assert radiusUserDto.getId() != null;
        RadiusUser radiusUser = conversionService.convert(radiusUserDto, RadiusUser.class);
        radiusUser = radiusUserRepo.save(radiusUser);
        return conversionService.convert(radiusUser, RadiusUserDto.class);
    }

    public void deleteRadiusUser(@NonNull RadiusUserDto radiusUserDto) {
        radiusUserRepo.deleteById(radiusUserDto.getId());
    }

    public void loadRadiusUsersFromRadiusDB() {
        Set<String> radCheckUsernames = radCheckRepo.getUsernames();
        Set<String> radReplyUsernames = radReplyRepo.getUsernames();

        Set<String> usernames = new HashSet<>();
        usernames.addAll(radCheckUsernames);
        usernames.addAll(radReplyUsernames);

        usernames.forEach(username -> {
            try {
                if (!radiusUserRepo.exists(QRadiusUser.radiusUser.username.like(username))) {
                    RadiusUser radiusUser = new RadiusUser();
                    radiusUser.setUsername(username);
                    radiusUserRepo.save(radiusUser);
                }
            } catch (Exception ignored) {
            }
        });
    }

    public long countRadiusUsers(@NonNull RadiusUserFilter filter) {
        return radiusUserRepo.count(buildRadiusUserSearchPredicate(filter));
    }

    public Page<RadiusUserDto> pageRadiusUsers(@NonNull RadiusUserFilter filter,
                                               @NonNull Pageable pageable) {
        Page<RadiusUser> page = radiusUserRepo.findAll(buildRadiusUserSearchPredicate(filter), pageable);
        List<RadiusUserDto> userDtos = page.stream()
                .map(user -> conversionService.convert(user, RadiusUserDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }

    public RadiusGroupDto createRadiusUsersGroup(@NonNull RadiusGroupDto radiusGroupDto) {
        assert radiusGroupDto.getId() == null;
        RadiusGroup radiusGroup = conversionService.convert(radiusGroupDto, RadiusGroup.class);
        radiusGroup = radiusGroupRepo.save(radiusGroup);
        return conversionService.convert(radiusGroup, RadiusGroupDto.class);
    }

    public RadiusGroupDto updateRadiusUsersGroup(@NonNull RadiusGroupDto radiusGroupDto) {
        assert radiusGroupDto.getId() != null;
        RadiusGroup radiusGroup = conversionService.convert(radiusGroupDto, RadiusGroup.class);
        radiusGroup = radiusGroupRepo.save(radiusGroup);
        return conversionService.convert(radiusGroup, RadiusGroupDto.class);
    }

    public void deleteRadiusUsersGroup(@NonNull RadiusGroupDto radiusGroup) {
        radiusGroupRepo.deleteById(radiusGroup.getId());
    }

    public void loadRadiusGroupsFromRadiusDB() {
        Set<String> radCheckGroupNames = radGroupCheckRepo.getGroupNames();
        Set<String> radReplyGroupName = radGroupReplyRepo.getGroupNames();

        Set<String> groupNames = new HashSet<>();
        groupNames.addAll(radCheckGroupNames);
        groupNames.addAll(radReplyGroupName);

        groupNames.forEach(name -> {
            RadiusGroup radiusGroup = new RadiusGroup();
            radiusGroup.setName(name);
            radiusGroupRepo.save(radiusGroup);
        });
    }

    public long countRadiusUsersGroup(@NonNull RadiusGroupFilter filter) {
        return radiusGroupRepo.count(buildRadiusGroupSearchPredicate(filter));
    }

    public Page<RadiusGroupDto> pageRadiusUsersGroup(@NonNull RadiusGroupFilter filter,
                                                     @NonNull Pageable pageable) {
        Page<RadiusGroup> page = radiusGroupRepo.findAll(buildRadiusGroupSearchPredicate(filter), pageable);
        List<RadiusGroupDto> userDtos = page.stream()
                .map(user -> conversionService.convert(user, RadiusGroupDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, userDtos.size());
    }

    private Predicate buildRadiusUserSearchPredicate(RadiusUserFilter filter) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(filter.getSearchText())) {
            if (filter.isSearchByName()) {
                booleanBuilder.or(QRadiusUser.radiusUser.username.contains(filter.getSearchText()));
            }
            if (filter.isSearchByDescription()) {
                booleanBuilder.or(QRadiusUser.radiusUser.description.contains(filter.getSearchText()));
            }
        }
        return booleanBuilder;
    }

    private Predicate buildRadiusGroupSearchPredicate(RadiusGroupFilter filter) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(filter.getSearchText())) {
            if (filter.isSearchByGroupName()) {
                booleanBuilder.or(QRadiusGroup.radiusGroup.name.contains(filter.getSearchText()));
            }
            if (filter.isSearchByDescription()) {
                booleanBuilder.or(QRadiusGroup.radiusGroup.description.contains(filter.getSearchText()));
            }
        }
        return booleanBuilder;
    }

}
