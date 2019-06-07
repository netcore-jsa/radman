package software.netcore.radman.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.accounting.AccountingService;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.support.Filter;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("Radman: Accounting")
@Route(value = "tutorial", layout = MainTemplate.class)
public class AccountingView extends VerticalLayout {

    private final Filter filter = new Filter();
    private final AccountingService accountingService;

    @Autowired
    public AccountingView(AccountingService accountingService) {
        this.accountingService = accountingService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        Grid<AccountingDto> grid = new Grid<>(AccountingDto.class, true);
        DataProvider<AccountingDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> accountingService.pageAccountingRecords(filter, pageable),
                value -> accountingService.countAccountingRecords(filter))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        TextField search = new TextField(event -> {
            filter.setSearchText(event.getValue());
            grid.getDataProvider().refreshAll();
        });
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setPlaceholder("Search...");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("Accounting"));
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

}
