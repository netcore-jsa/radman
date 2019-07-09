package software.netcore.radman.ui.view;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.dto.LoadingResult;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserFilter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.notification.LoadingResultNotification;

import java.util.Objects;
import java.util.Optional;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: Users")
@Route(value = "users", layout = MenuTemplate.class)
public class UsersView extends VerticalLayout {

    private static final long serialVersionUID = -6514992348061184693L;

    private final RadiusUserFilter filter = new RadiusUserFilter(true, true);
    private final RadiusUserService userService;
    private final SecurityService securityService;

    @Autowired
    public UsersView(RadiusUserService userService, SecurityService securityService) {
        this.userService = userService;
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
                (pageable, o) -> userService.pageRadiusUsers(filter, pageable),
                value -> userService.countRadiusUsers(filter))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setDataProvider(dataProvider);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        UserCreationDialog creationDialog = new UserCreationDialog(
                (source, bean) -> grid.getDataProvider().refreshAll());
        UserEditDialog editDialog = new UserEditDialog(
                (source, bean) -> grid.getDataProvider().refreshItem(bean));

        Checkbox removeFromRadius = new Checkbox("Remove from Radius");
        ConfirmationDialog deleteDialog = new ConfirmationDialog("400px");
        deleteDialog.setTitle("Delete Radius user");
        deleteDialog.setConfirmButtonCaption("Delete");
        deleteDialog.setConfirmListener(() -> {
            Optional<RadiusUserDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(radiusUserDto -> {
                try {
                    userService.deleteRadiusUser(radiusUserDto, removeFromRadius.getValue());
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to delete user. Reason = '{}'", e.getMessage());
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
            RadiusUserDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                editDialog.edit(dto);
                deleteDialog.setOpened(false);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> {
            Optional<RadiusUserDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(radiusUserDto -> {
                deleteDialog.setContent(removeFromRadius, new Label("Are you sure you want to delete '" +
                        radiusUserDto.getUsername() + "' user?"));
                deleteDialog.setOpened(true);
            });
        });
        deleteBtn.setEnabled(false);
        Button loadUsers = new Button("Load from Radius", event -> {
            LoadingResult result = userService.loadRadiusUsersFromRadiusDB();
            LoadingResultNotification.show("Users load result", result);
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

    private abstract class UserFormDialog extends Dialog {

        final Binder<RadiusUserDto> binder;

        UserFormDialog() {
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

    private class UserCreationDialog extends UserFormDialog {

        private final CreationListener<RadiusUserDto> creationListener;

        UserCreationDialog(CreationListener<RadiusUserDto> creationListener) {
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
                        dto = userService.createRadiusUser(dto);
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

    private class UserEditDialog extends UserFormDialog {

        private final UpdateListener<RadiusUserDto> updateListener;

        UserEditDialog(UpdateListener<RadiusUserDto> updateListener) {
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
                        dto = userService.updateRadiusUser(dto);
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
