package software.netcore.radman.buisness.service.nas;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.entity.QNas;
import software.netcore.radman.data.radius.entity.QRadHuntGroup;
import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.radius.repo.NasRepo;
import software.netcore.radman.data.radius.repo.RadHuntGroupRepo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @since v. 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class NasService {

    private final NasRepo nasRepo;
    private final RadHuntGroupRepo radHuntGroupRepo;
    private final ConversionService conversionService;

    public NasDto createNas(@NonNull NasDto nasDto) {
        Nas nas = conversionService.convert(nasDto, Nas.class);
        nas = nasRepo.save(nas);
        return conversionService.convert(nas, NasDto.class);
    }

    public NasGroupDto createNasGroup(@NonNull NasGroupDto nasGroupDto) {
        RadHuntGroup radHuntGroup = conversionService.convert(nasGroupDto, RadHuntGroup.class);
        radHuntGroup = radHuntGroupRepo.save(radHuntGroup);
        return conversionService.convert(radHuntGroup, NasGroupDto.class);
    }

    public NasDto updateNas(@NonNull NasDto nasDto) {
        Nas nasUpdate = conversionService.convert(nasDto, Nas.class);
        nasUpdate = nasRepo.save(nasUpdate);
        return conversionService.convert(nasUpdate, NasDto.class);
    }

    public boolean existsNasWithName(String name) {
        return nasRepo.existsByNasName(name);
    }

    public NasGroupDto updateNasGroup(@NonNull NasGroupDto nasGroupDto) {
        RadHuntGroup radHuntGroup = conversionService.convert(nasGroupDto, RadHuntGroup.class);
        radHuntGroup = radHuntGroupRepo.save(radHuntGroup);
        return conversionService.convert(radHuntGroup, NasGroupDto.class);
    }

    public boolean existsNasGroupWithIpAddress(String ipAddress) {
        return radHuntGroupRepo.existsByNasIpAddress(ipAddress);
    }

    public void deleteNas(@NonNull NasDto nasDto) {
        nasRepo.deleteById(nasDto.getId());
    }

    public void deleteNasGroup(@NonNull NasGroupDto nasGroupDto) {
        radHuntGroupRepo.deleteById(nasGroupDto.getId());
    }

    public long countNasRecords(@Nullable String searchText) {
        return nasRepo.count(buildNasSearchPredicate(searchText));
    }

    public Page<NasDto> pageNasRecords(@Nullable String searchText, @NonNull Pageable pageable) {
        Page<Nas> page = nasRepo.findAll(buildNasSearchPredicate(searchText), pageable);
        List<NasDto> nasDtos = page.stream()
                .map(nas -> conversionService.convert(nas, NasDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(nasDtos, pageable, nasDtos.size());
    }

    public long countNasGroupRecords(@Nullable String searchText) {
        return radHuntGroupRepo.count(buildNasGroupSearchPredicate(searchText));
    }

    public Page<NasGroupDto> pageNasGroupRecords(@Nullable String searchText, @NonNull Pageable pageable) {
        Page<RadHuntGroup> page = radHuntGroupRepo.findAll(buildNasGroupSearchPredicate(searchText), pageable);
        List<NasGroupDto> nasGroupDtos = page.stream()
                .map(radHuntGroup -> conversionService.convert(radHuntGroup, NasGroupDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(nasGroupDtos, pageable, nasGroupDtos.size());
    }

    private Predicate buildNasSearchPredicate(@Nullable String searchText) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(searchText)) {
            booleanBuilder.or(QNas.nas.nasName.contains(searchText));
            booleanBuilder.or(QNas.nas.shortName.contains(searchText));
            booleanBuilder.or(QNas.nas.description.contains(searchText));
            booleanBuilder.or(QNas.nas.ports.like(searchText));
            booleanBuilder.or(QNas.nas.secret.contains(searchText));
            booleanBuilder.or(QNas.nas.server.contains(searchText));
            booleanBuilder.or(QNas.nas.type.contains(searchText));
        }
        return booleanBuilder.getValue();
    }

    private Predicate buildNasGroupSearchPredicate(@Nullable String searchText) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(searchText)) {
            booleanBuilder.or(QRadHuntGroup.radHuntGroup.groupName.contains(searchText));
            booleanBuilder.or(QRadHuntGroup.radHuntGroup.nasIpAddress.contains(searchText));
            booleanBuilder.or(QRadHuntGroup.radHuntGroup.nasPortId.contains(searchText));
        }
        return booleanBuilder;
    }

}
