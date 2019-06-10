package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserFilter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: Users")
@Route(value = "users", layout = MainTemplate.class)
public class UsersView extends VerticalLayout {

    private final RadiusUserFilter filter = new RadiusUserFilter(true, true);
    private final RadiusUserService service;
    private final SecurityService securityService;

    @Autowired
    public UsersView(RadiusUserService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        Grid<RadiusUserDto> grid = new Grid<>(RadiusUserDto.class, false);
        grid.addColumns("username", "description");
        DataProvider<RadiusUserDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> service.pageRadiusUsers(filter, pageable),
                value -> service.countRadiusUsers(filter))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setDataProvider(dataProvider);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        UserCreationDialog creationDialog = new UserCreationDialog(service,
                (source, bean) -> grid.getDataProvider().refreshAll());
        UserEditDialog editDialog = new UserEditDialog(service,
                (source, bean) -> grid.getDataProvider().refreshItem(bean));
        ConfirmationDialog deleteDialog = new ConfirmationDialog("250px");
        deleteDialog.setTitle("Delete Radius user");
        deleteDialog.setDescription("Are you sure?");
        deleteDialog.setConfirmButtonCaption("Delete");
        deleteDialog.setConfirmListener(() -> {
            RadiusUserDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                try {
                    service.deleteRadiusUser(dto);
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to delete user. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
                deleteDialog.setOpened(false);
            }
        });

        Button createBtn = new Button("Create", event -> creationDialog.startCreation());
        createBtn.setEnabled(role == RoleDto.ADMIN);
        Button editBtn = new Button("Edit", event -> {
            RadiusUserDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                editDialog.edit(dto);
                deleteDialog.setOpened(false);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> deleteDialog.setOpened(true));
        deleteBtn.setEnabled(false);
        Button loadUsers = new Button("Load from Radius", event -> {
            service.loadRadiusUsersFromRadiusDB();
            grid.getDataProvider().refreshAll();
        });
        loadUsers.setEnabled(role == RoleDto.ADMIN);

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
        horizontalLayout.add(loadUsers);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

    static abstract class UserFormDialog extends Dialog {

        final RadiusUserService service;
        final Binder<RadiusUserDto> binder;

        UserFormDialog(RadiusUserService service) {
            this.service = service;

            TextField username = new TextField("Username");
            username.setValueChangeMode(ValueChangeMode.EAGER);
            TextField description = new TextField("Description");
            description.setValueChangeMode(ValueChangeMode.EAGER);

            binder = new BeanValidationBinder<>(RadiusUserDto.class);
            binder.bind(username, "username");
            binder.bind(description, "description");

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
            controlsLayout.add(getConfirmBtn());
            controlsLayout.setWidthFull();

            add(new H3(getDialogTitle()));
            add(new FormLayout(username, description));
            add(new Hr());
            add(controlsLayout);
        }

        abstract String getDialogTitle();

        abstract Button getConfirmBtn();

    }

    static class UserCreationDialog extends UserFormDialog {

        private final CreationListener<RadiusUserDto> creationListener;

        UserCreationDialog(RadiusUserService radiusUserService,
                           CreationListener<RadiusUserDto> creationListener) {
            super(radiusUserService);
            this.creationListener = creationListener;
        }

        @Override
        String getDialogTitle() {
            return "Create Radius user";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Create", event -> {
                RadiusUserDto dto = new RadiusUserDto();
                if (binder.writeBeanIfValid(dto)) {
                    try {
                        dto = service.createRadiusUser(dto);
                        creationListener.onCreated(this, dto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to create radius user. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void startCreation() {
            setOpened(true);
            binder.readBean(new RadiusUserDto());
        }

    }

    static class UserEditDialog extends UserFormDialog {

        private final UpdateListener<RadiusUserDto> updateListener;

        UserEditDialog(RadiusUserService radiusUserService,
                       UpdateListener<RadiusUserDto> updateListener) {
            super(radiusUserService);
            this.updateListener = updateListener;
        }

        @Override
        String getDialogTitle() {
            return "Edit Radius user";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Save", event -> {
                BinderValidationStatus<RadiusUserDto> validationStatus = binder.validate();
                if (validationStatus.isOk()) {
                    try {
                        RadiusUserDto dto = binder.getBean();
                        dto = service.updateRadiusUser(dto);
                        updateListener.onUpdated(this, dto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to update Radius user. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void edit(RadiusUserDto dto) {
            setOpened(true);
            binder.setBean(dto);
        }

    }

}
