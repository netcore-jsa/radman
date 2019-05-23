package software.netcore.radman.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: NAS groups")
@Route(value = "nas_groups", layout = MainTemplate.class)
public class NasGroupsView extends Div {

    private final NasService nasService;

    @Autowired
    public NasGroupsView(NasService nasService) {
        this.nasService = nasService;
        buildView();
    }

    private void buildView() {
        Grid<NasGroupDto> grid = new Grid<>(NasGroupDto.class, false);
        grid.setColumns("groupName", "nasIpAddress", "nasPortId");
        DataProvider<NasGroupDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> nasService.pageNasGroupRecords(pageable),
                value -> nasService.countNasGroupRecords())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setDataProvider(dataProvider);
        add(grid);
    }

}
