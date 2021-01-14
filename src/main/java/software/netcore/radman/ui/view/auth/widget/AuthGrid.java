package software.netcore.radman.ui.view.auth.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import lombok.extern.slf4j.Slf4j;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.auth.dto.AuthDto;
import software.netcore.radman.buisness.service.auth.dto.AuthsDto;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AuthGrid<T extends AuthsDto, U extends AuthDto> extends Div {

    private final Filter filter = new Filter();
    private final ConfirmationDialog deleteDialog;
    final Grid<Map<String, String>> grid;

    AuthGrid(SecurityService securityService) {
        setWidth("100%");

        RoleDto role = securityService.getLoggedUserRole();
        grid = new Grid<>();
        deleteDialog = new ConfirmationDialog("400px");
        deleteDialog.setTitle("Delete assigned attributes");
        deleteDialog.setConfirmListener(() -> {
            Optional<Map<String, String>> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(row -> {
                String name = row.get("name");
                String type = row.get("type");
                try {
                    deleteAssigment(name, type);
                    deleteDialog.setOpened(false);
                    refreshGrid();
                } catch (Exception e) {
                    log.warn("Failed to delete auth. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            });
        });

        Button assignBtn = new Button("Assign attribute", event -> getAssigmentDialog().startAssigment());
        assignBtn.setEnabled(role == RoleDto.ADMIN);
        Button deleteBtn = new Button("Delete", event -> {
            Optional<Map<String, String>> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(row -> {
                deleteDialog.setDescription("Are you sure you want to delete '" +
                        row.get("name") + "' user and its attributes?");
                deleteDialog.setOpened(true);
            });
        });
        deleteBtn.setEnabled(false);

        grid.asSingleSelect().addValueChangeListener(event ->
                deleteBtn.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN));

        TextField search = new TextField(event -> {
            filter.setSearchText(event.getValue());
            refreshGrid();
        });
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setPlaceholder("Search...");
        search.setMinWidth("30px");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3(getGridTitle()));
        horizontalLayout.add(assignBtn);
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);

//        refreshGrid();
    }

    protected void refreshGrid() {
        AuthsDto authsDto = getAuthsDto(filter);
        if (grid.getColumns().size() == 0) {
            authsDto.getColumnsSpec().keySet().forEach(key
                    -> grid.addColumn((ValueProvider<Map<String, String>, Object>) map
                    -> map.get(key)).setHeader(key));
            grid.getColumns().forEach(column -> column.setSortable(true));
        }
        grid.setItems(authsDto.getData());
    }

    abstract String getGridTitle();

    abstract T getAuthsDto(Filter filter);

    abstract AttributeAssignmentDialog<U, ? extends AttributeDto> getAssigmentDialog();

    abstract void deleteAssigment(String name, String type);

}
