package software.netcore.radman.ui.view.nas;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;
import software.netcore.radman.ui.view.nas.widget.NasCreateDialog;
import software.netcore.radman.ui.view.nas.widget.NasEditDialog;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: NAS")
@Route(value = "", layout = MenuTemplate.class)
public class NasView extends VerticalLayout {

    private static final long serialVersionUID = 1177928981515973375L;

    private final Filter filter = new Filter();
    private final NasService nasService;
    private final SecurityService securityService;

    @Autowired
    public NasView(NasService nasService, SecurityService securityService) {
        this.nasService = nasService;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        Grid<NasDto> grid = new Grid<>(NasDto.class, false);
        grid.addColumns("nasName", "shortName", "description");
        grid.addColumn((ValueProvider<NasDto, String>) nasDto
                -> nasDto.getSecret().replaceAll(".", "*")).setHeader("Secret");
        grid.addColumns("server", "community", "ports", "type");
        DataProvider<NasDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> nasService.pageNasRecords(filter.getSearchText(), pageable),
                value -> nasService.countNasRecords(filter.getSearchText()))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        ConfirmationDialog nasDeleteDialog = new ConfirmationDialog("400px");
        nasDeleteDialog.setTitle("Delete NAS");
        nasDeleteDialog.setConfirmButtonCaption("Delete");
        nasDeleteDialog.setConfirmListener(() -> {
            NasDto nasDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(nasDto)) {
                try {
                    nasService.deleteNas(nasDto);
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to delete NAS. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            }
            nasDeleteDialog.setOpened(false);
        });

        NasEditDialog nasEditDialog = new NasEditDialog(nasService,
                (source, bean) -> grid.getDataProvider().refreshItem(bean));
        NasCreateDialog nasCreateDialog = new NasCreateDialog(nasService,
                (source, bean) -> grid.getDataProvider().refreshAll());

        Button createBtn = new Button("Create", event -> nasCreateDialog.startNasCreation());
        createBtn.setEnabled(role == RoleDto.ADMIN);
        Button editBtn = new Button("Edit", event -> {
            NasDto nasDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(nasDto)) {
                nasEditDialog.editNas(nasDto);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> {
            NasDto nasDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(nasDto)) {
                nasDeleteDialog.setDescription("Are you sure you want to delete '" + nasDto.getNasName() + "' NAS?");
                nasDeleteDialog.setOpened(true);
            }
        });
        deleteBtn.setEnabled(false);

        grid.asSingleSelect().addValueChangeListener(event -> {
            editBtn.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN);
            deleteBtn.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN);
        });

        TextField search = new TextField(event -> {
            filter.setSearchText(event.getValue());
            grid.getDataProvider().refreshAll();
        });
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setPlaceholder("Search...");

        add(new H4("Data from Radius DB - \"nas\" table"));
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("NAS"));
        horizontalLayout.add(createBtn);
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

}
