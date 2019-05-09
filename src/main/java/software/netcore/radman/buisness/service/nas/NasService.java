package software.netcore.radman.buisness.service.nas;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.radius.repo.NasRepo;
import software.netcore.radman.data.radius.repo.RadHuntGroupRepo;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NasService {

    private final NasRepo nasRepo;
    private final RadHuntGroupRepo radHuntGroupRepo;
    private final ConversionService conversionService;

    public int countNasRecords() {
        return (int) nasRepo.count();
    }

    public Page<NasDto> pageNasRecords(Pageable pageable) {
        Page<Nas> page = nasRepo.pageNasRecords(pageable);
        List<NasDto> nasDtos = page.stream()
                .map(nas -> conversionService.convert(nas, NasDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(nasDtos, pageable, nasDtos.size());
    }

    public int countNasGroupRecords() {
        return (int) radHuntGroupRepo.count();
    }

    public Page<NasGroupDto> pageNasGroupRecords(Pageable pageable) {
        Page<RadHuntGroup> page = radHuntGroupRepo.pageRadHuntGroupRecords(pageable);
        List<NasGroupDto> nasGroupDtos = page.stream()
                .map(radHuntGroup -> conversionService.convert(radHuntGroup, NasGroupDto.class))
                .collect(Collectors.toList());
        return new PageImpl<>(nasGroupDtos, pageable, nasGroupDtos.size());
    }


    // -------------------  DEV STAFF - will be removed  --------------------------------------------------

    @PostConstruct
    void init() {
        generate();
    }

    private void generate() {
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
