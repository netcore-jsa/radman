package software.netcore.radman.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.accounting.AccountingService;
import software.netcore.radman.buisness.service.accounting.dto.AccountingDto;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("Radman: Accounting")
@Route(value = "tutorial", layout = MainTemplate.class)
public class AccountingView extends Div {

    private final AccountingService accountingService;

    @Autowired
    public AccountingView(AccountingService accountingService) {
        this.accountingService = accountingService;
        buildView();
    }

    private void buildView() {
        Grid<AccountingDto> accountingGrid = new Grid<>(AccountingDto.class, true);
        DataProvider<AccountingDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> accountingService.pageAccountingRecords(pageable),
                value -> accountingService.countAccountingRecords())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        accountingGrid.setDataProvider(dataProvider);
        accountingGrid.getColumns().forEach(column -> column.setResizable(true));
        accountingGrid.setColumnReorderingAllowed(true);
        add(accountingGrid);
    }

}
