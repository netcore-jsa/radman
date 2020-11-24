package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import lombok.NonNull;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.ui.component.wizard.WizardStep;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class NasGroupStep implements WizardStep<NewEntityWizardDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final Binder<NasGroupDto> binder = new Binder<>();
    private final TextField newGroupName;
    private final Grid<NasDto> grid;

    public NasGroupStep(NasService nasService) {
        newGroupName = new TextField("New group name");
        newGroupName.setRequired(true);
        binder.forField(newGroupName)
                .bind(NasGroupDto::getGroupName, NasGroupDto::setGroupName);

        grid = new Grid<>(NasDto.class, false);
        grid.addColumns("nasName", "shortName", "description", "type");
        DataProvider<NasDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> nasService.pageNasRecords(null, pageable),
                value -> nasService.countNasRecords(null))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setMinHeight("200px");
        grid.setHeight("100%");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        contentLayout.withComponent(newGroupName)
                .withComponent(grid);
    }

    @Override
    public Component getContent() {
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return binder.validate().isOk();
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        for (NasDto selected : grid.getSelectedItems()) {
            NasGroupDto nasGroupDto = new NasGroupDto();
            nasGroupDto.setGroupName(newGroupName.getValue());
            nasGroupDto.setNasIpAddress(selected.getNasName());
            dataStorage.getNasGroupDtos().add(nasGroupDto);
        }
    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

}
