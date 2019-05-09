package software.netcore.radman.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: NAS")
@Route(value = "", layout = MainTemplate.class)
public class NasView extends Div {

    @Autowired
    public NasView(NasService nasService) {
        Grid<NasDto> grid = new Grid<>(NasDto.class, false);
        grid.addColumns("nasName", "shortName", "description");
        grid.addColumn((ValueProvider<NasDto, String>) nasDto
                -> nasDto.getSecret().replaceAll(".", "*")).setHeader("Secret");
        grid.addColumns("server", "community", "ports", "type");
        DataProvider<NasDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> nasService.pageNasRecords(pageable), value -> nasService.countNasRecords())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        add(grid);
    }

}
