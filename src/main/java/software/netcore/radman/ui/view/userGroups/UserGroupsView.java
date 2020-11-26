package software.netcore.radman.ui.view.userGroups;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.dto.LoadingResult;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupFilter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.notification.LoadingResultNotification;
import software.netcore.radman.ui.view.userGroups.widget.UserGroupCreationDialog;
import software.netcore.radman.ui.view.userGroups.widget.UserGroupEditDialog;

import java.util.Objects;
import java.util.Optional;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: User groups")
@Route(value = "user_groups", layout = MenuTemplate.class)
public class UserGroupsView extends VerticalLayout {

    private static final long serialVersionUID = 3021806251927346711L;

    private final RadiusGroupFilter filter = new RadiusGroupFilter();
    private final RadiusUserService service;
    private final SecurityService securityService;

    public UserGroupsView(RadiusUserService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        Grid<RadiusGroupDto> grid = new Grid<>(RadiusGroupDto.class, false);
        grid.addColumns("name", "description");
        DataProvider<RadiusGroupDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> service.pageRadiusUsersGroup(filter, pageable),
                value -> service.countRadiusUsersGroup(filter))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setDataProvider(dataProvider);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        UserGroupCreationDialog creationDialog = new UserGroupCreationDialog(service,
                (source, bean) -> grid.getDataProvider().refreshAll());
        UserGroupEditDialog editDialog = new UserGroupEditDialog(service,
                (source, bean) -> grid.getDataProvider().refreshItem(bean));

        Checkbox removeFromRadius = new Checkbox("Remove from Radius");
        ConfirmationDialog deleteDialog = new ConfirmationDialog("400px");
        deleteDialog.setTitle("Delete Radius group");
        deleteDialog.setContent(removeFromRadius, new Label("Are you sure?"));
        deleteDialog.setConfirmButtonCaption("Delete");
        deleteDialog.setConfirmListener(() -> {
            Optional<RadiusGroupDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(radiusGroupDto -> {
                try {
                    service.deleteRadiusUsersGroup(radiusGroupDto, removeFromRadius.getValue());
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to delete users group. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            });
            deleteDialog.setOpened(false);
        });
        deleteDialog.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                removeFromRadius.setValue(false);
            }
        });

        Button createBtn = new Button("Create", event -> creationDialog.startCreation());
        createBtn.setEnabled(role == RoleDto.ADMIN);
        Button editBtn = new Button("Edit", event -> {
            RadiusGroupDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                editDialog.edit(dto);
                deleteDialog.setOpened(false);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> {
            Optional<RadiusGroupDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(radiusGroupDto -> {
                deleteDialog.setContent(removeFromRadius, new Label("Are you sure you want to delete '" +
                        radiusGroupDto.getName() + "' user group?"));
                deleteDialog.setOpened(true);
            });
        });
        deleteBtn.setEnabled(false);
        Button loadUserGroups = new Button("Load from Radius", event -> {
            LoadingResult result = service.loadRadiusGroupsFromRadiusDB();
            LoadingResultNotification.show("Groups load result", result);
            grid.getDataProvider().refreshAll();
        });
        loadUserGroups.setEnabled(role == RoleDto.ADMIN);

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

        add(new H4("Data from RadMan DB"));
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("Users"));
        horizontalLayout.add(createBtn);
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(loadUserGroups);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

}
