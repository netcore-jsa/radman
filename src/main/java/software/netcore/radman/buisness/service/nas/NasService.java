package software.netcore.radman.buisness.service.nas;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.exception.NotFoundException;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.entity.QNas;
import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.radius.repo.NasRepo;
import software.netcore.radman.data.radius.repo.RadHuntGroupRepo;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
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

    public NasDto createNas(NasDto nasDto) {
        Nas nas = conversionService.convert(nasDto, Nas.class);
        nas = nasRepo.save(nas);
        return conversionService.convert(nas, NasDto.class);
    }

    public NasGroupDto createNasGroup(NasGroupDto nasGroupDto) {
        RadHuntGroup radHuntGroup = conversionService.convert(nasGroupDto, RadHuntGroup.class);
        radHuntGroup = radHuntGroupRepo.save(radHuntGroup);
        return conversionService.convert(radHuntGroup, NasGroupDto.class);
    }

    @Transactional
    public NasDto updateNas(NasDto nasDto) throws NotFoundException {
        assert nasDto.getId() != null;
        Nas nasUpdate = conversionService.convert(nasDto, Nas.class);
        Nas nas = nasRepo.findById(nasUpdate.getId()).orElse(null);
        if (Objects.isNull(nas)) {
            log.info("Failed to update NAS. Nas with name '{}' and id '{}' not found",
                    nasDto.getNasName(), nasDto.getId());
            throw new NotFoundException("Failed to update NAS. Not found");
        }
        if (!Objects.equals(nasUpdate.getNasName(), nas.getNasName())) {
            List<RadHuntGroup> radHuntGroups = radHuntGroupRepo.findByNasIpAddress(nas.getNasName());
            radHuntGroups.forEach(radHuntGroup -> radHuntGroup.setNasIpAddress(nasDto.getNasName()));
            radHuntGroupRepo.saveAll(radHuntGroups);
        }
        nasUpdate = nasRepo.save(nasUpdate);
        return conversionService.convert(nasUpdate, NasDto.class);
    }

    public NasGroupDto updateNasGroup(NasGroupDto nasGroupDto) {
        assert nasGroupDto.getId() != null;
        RadHuntGroup radHuntGroup = conversionService.convert(nasGroupDto, RadHuntGroup.class);
        radHuntGroup = radHuntGroupRepo.save(radHuntGroup);
        return conversionService.convert(radHuntGroup, NasGroupDto.class);
    }

    public void deleteNas(NasDto nasDto) {
        nasRepo.deleteById(nasDto.getId());
    }

    public void deleteNasGroup(NasGroupDto nasGroupDto) {
        radHuntGroupRepo.deleteById(nasGroupDto.getId());
    }

    public long countNasRecords(String searchText) {
        return nasRepo.count(buildNasEntitySearchPredicate(searchText));
    }

    public Page<NasDto> pageNasRecords(String searchText, Pageable pageable) {
        Page<Nas> page = nasRepo.findAll(buildNasEntitySearchPredicate(searchText), pageable);
        List<NasDto> nasDtos = page.stream()
                .map(nas -> conversionService.convert(nas, NasDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(nasDtos, pageable, nasDtos.size());
    }

    public long countNasGroupRecords() {
        return radHuntGroupRepo.count();
    }

    public Page<NasGroupDto> pageNasGroupRecords(Pageable pageable) {
        Page<RadHuntGroup> page = radHuntGroupRepo.pageRadHuntGroupRecords(pageable);
        List<NasGroupDto> nasGroupDtos = page.stream()
                .map(radHuntGroup -> conversionService.convert(radHuntGroup, NasGroupDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(nasGroupDtos, pageable, nasGroupDtos.size());
    }

    private Predicate buildNasEntitySearchPredicate(String searchText) {
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

    // -------------------  DEV STAFF - will be removed  --------------------------------------------------

    @PostConstruct
    void init() {
        generate();
    }

    private void generate() {
        if (nasRepo.count() == 0) {
            for (int i = 0; i < 1000; i++) {
                Nas nas = new Nas();
                nas.setId(i);
                nas.setNasName("name" + i);
                nas.setCommunity("community" + i);
                nas.setDescription("description");
                nas.setPorts(5);
                nas.setSecret("secret" + i);
                nas.setShortName("short" + i);
                nas.setType("type" + i);
                nas.setServer("server" + i);
                nasRepo.save(nas);

                RadHuntGroup group = new RadHuntGroup();
                group.setId(i);
                group.setGroupName("groupName" + i);
                group.setNasIpAddress("nasIpAddress" + i);
                group.setNasPortId("nasPortId" + i);
                radHuntGroupRepo.save(group);
            }
        }
    }

}
