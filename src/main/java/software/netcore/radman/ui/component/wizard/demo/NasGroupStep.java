package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import lombok.NonNull;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.ui.component.wizard.WizardStep;

public class NasGroupStep implements WizardStep<NasGroupDto> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();

    public NasGroupStep(NasService nasService) {
        VTextField newGroupName = new VTextField("New group name")
                .withRequired(true);

        Grid<NasDto> grid = new Grid<>(NasDto.class, false);
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

        contentLayout.withComponent(newGroupName)
                .withComponent(grid);
    }

    @Override
    public Component getContent() {
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return true; //TODO wizard - check binder
    }

    @Override
    public void writeDataToStorage(@NonNull NasGroupDto dataStorage) {

    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

}
