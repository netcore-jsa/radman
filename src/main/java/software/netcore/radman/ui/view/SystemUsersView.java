package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: System users")
@Route(value = "system_users", layout = MainTemplate.class)
public class SystemUsersView extends VerticalLayout {

    private final Filter filter = new Filter();
    private final SystemUserService service;
    private final SecurityService securityService;

    @Autowired
    public SystemUsersView(SystemUserService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
        buildView();
    }

    @SuppressWarnings("Duplicates")
    private void buildView() {
        setHeightFull();
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        RoleDto role = securityService.getLoggedUserRole();
        if (role == RoleDto.READ_ONLY) {
            add(new H2("Sorry, you don't have access to this view"));
        } else {
            Grid<SystemUserDto> grid = new Grid<>(SystemUserDto.class, false);
            grid.addColumns("username", "role");
            grid.addColumn(new LocalDateTimeRenderer<>(systemUserDto -> {
                if (systemUserDto.getLastLoginTime() == null) {
                    return null;
                }
                return LocalDateTime.ofEpochSecond(systemUserDto.getLastLoginTime(), 0,
                        OffsetDateTime.now().getOffset());
            }, DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm, a", Locale.US), "never"))
                    .setSortable(true)
                    .setHeader("Last login time")
                    .setSortProperty("lastLoginTime");
            grid.addColumn("authProvider").setHeader("Authentication provider");
            DataProvider<SystemUserDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                    (pageable, o) -> service.pageSystemUsers(filter.getSearchText(), pageable),
                    value -> service.countSystemUsers(filter.getSearchText()))
                    .withDefaultSort("id", SortDirection.ASCENDING)
                    .build();
            grid.getColumns().forEach(column -> column.setResizable(true));
            grid.setColumnReorderingAllowed(true);
            grid.setDataProvider(dataProvider);
            grid.setMinHeight("500px");
            grid.setHeight("100%");

            SystemUserCreationDialog createDialog = new SystemUserCreationDialog(service,
                    (source, bean) -> grid.getDataProvider().refreshAll());
            SystemUserEditDialog editDialog = new SystemUserEditDialog(service,
                    (source, bean) -> grid.getDataProvider().refreshItem(bean));
            ConfirmationDialog deleteDialog = new ConfirmationDialog();
            deleteDialog.setTitle("Delete system user");
            deleteDialog.setConfirmButtonCaption("Confirm");
            deleteDialog.setConfirmListener(() -> {
                SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(user)) {
                    service.deleteSystemUser(user);
                    grid.getDataProvider().refreshAll();
                    deleteDialog.setOpened(false);
                }
            });

            Button createBtn = new Button("Create", event -> createDialog.startCreation());
            Button editBtn = new Button("Edit", event -> {
                SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(user)) {
                    editDialog.edit(user);
                }
            });
            editBtn.setEnabled(false);
            Button deleteBtn = new Button("Delete", event -> {
                SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(user)) {
                    deleteDialog.setDescription("Are you sure you want to delete '" + user.getUsername() + "' user?");
                    deleteDialog.setOpened(true);
                }
            });
            deleteBtn.setEnabled(false);

            grid.asSingleSelect().addValueChangeListener(event -> {
                editBtn.setEnabled(Objects.nonNull(event.getValue()));
                deleteBtn.setEnabled(Objects.nonNull(event.getValue()));
            });

            TextField search = new TextField(event -> {
                filter.setSearchText(event.getValue());
                grid.getDataProvider().refreshAll();
            });
            search.setValueChangeMode(ValueChangeMode.EAGER);
            search.setPlaceholder("Search...");

            add(new H4("From RadMan DB"));
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            horizontalLayout.add(new H3("System users"));
            horizontalLayout.add(createBtn);
            horizontalLayout.add(editBtn);
            horizontalLayout.add(deleteBtn);
            horizontalLayout.add(search);
            add(horizontalLayout);
            add(grid);
        }
    }

    static class SystemUserCreationDialog extends Dialog {

        private final Binder<SystemUserDto> binder;

        SystemUserCreationDialog(SystemUserService service,
                                 CreationListener<SystemUserDto> creationListener) {
            binder = new BeanValidationBinder<>(SystemUserDto.class);

            FormLayout formLayout = new FormLayout();
            formLayout.add(new H3("New system user"));
            TextField username = new TextField("Username");
            username.setValueChangeMode(ValueChangeMode.EAGER);
            username.setWidthFull();
            PasswordField password = new PasswordField("Password");
            password.setValueChangeMode(ValueChangeMode.EAGER);
            password.setWidthFull();
            ComboBox<RoleDto> role = new ComboBox<>("Role", RoleDto.values());
            role.setPreventInvalidInput(true);
            role.setWidthFull();
            ComboBox<AuthProviderDto> authProvider = new ComboBox<>("Authentication provider", AuthProviderDto.values());
            authProvider.setPreventInvalidInput(true);
            authProvider.setWidthFull();
            authProvider.addValueChangeListener(event -> {
                if (AuthProviderDto.LOCAL == event.getValue()) {
                    binder.bind(password, "password");
                    password.setVisible(true);
                } else {
                    binder.removeBinding("password");
                    password.setVisible(false);
                }
            });

            binder.bind(username, "username");
            binder.bind(role, "role");
            binder.bind(authProvider, "authProvider");

            Button createBtn = new Button("Create", event -> {
                SystemUserDto userDto = new SystemUserDto();
                if (binder.writeBeanIfValid(userDto)) {
                    try {
                        userDto = service.createSystemUser(userDto);
                        creationListener.onCreated(this, userDto);
                        setOpened(false);
                    } catch (DataIntegrityViolationException e) {
                        username.setInvalid(true);
                        username.setErrorMessage("User with the same username already exist.");
                    } catch (Exception e) {
                        log.warn("Failed to create system user. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
            Button cancelBtn = new Button("Cancel", event -> setOpened(false));
            HorizontalLayout controls = new HorizontalLayout();
            controls.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controls.add(cancelBtn, createBtn);
            controls.setWidthFull();

            formLayout.add(username);
            formLayout.add(password);
            formLayout.add(role);
            formLayout.add(authProvider);
            formLayout.add(new Hr());
            formLayout.add(controls);
            formLayout.setMaxWidth("400px");
            add(formLayout);
        }

        void startCreation() {
            SystemUserDto systemUserDto = new SystemUserDto();
            systemUserDto.setRole(RoleDto.ADMIN);
            systemUserDto.setAuthProvider(AuthProviderDto.LOCAL);
            binder.readBean(systemUserDto);
            setOpened(true);
        }

    }

    static class SystemUserEditDialog extends Dialog {

        private final Binder<SystemUserDto> binder;

        SystemUserEditDialog(SystemUserService service,
                             UpdateListener<SystemUserDto> updateListener) {
            FormLayout formLayout = new FormLayout();
            formLayout.add(new H3("Edit system user"));
            ComboBox<RoleDto> role = new ComboBox<>("Role", RoleDto.values());
            role.setPreventInvalidInput(true);
            role.setWidthFull();

            binder = new Binder<>(SystemUserDto.class);
            binder.forField(role)
                    .asRequired("Role is required")
                    .withValidator((Validator<RoleDto>) (value, context) -> {
                        if (value == null) {
                            return ValidationResult.error("System user access role is required.");
                        }
                        return ValidationResult.ok();
                    })
                    .bind(SystemUserDto::getRole, SystemUserDto::setRole);

            Button saveBtn = new Button("Save", event -> {
                BinderValidationStatus<SystemUserDto> validationStatus = binder.validate();
                if (validationStatus.isOk()) {
                    try {
                        SystemUserDto userDto = binder.getBean();
                        userDto = service.updateSystemUser(userDto);
                        updateListener.onUpdated(this, userDto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to update system user. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
            Button cancelBtn = new Button("Cancel", event -> setOpened(false));

            HorizontalLayout controls = new HorizontalLayout();
            controls.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controls.add(cancelBtn, saveBtn);
            controls.setWidthFull();

            formLayout.add(role);
            formLayout.add(new Hr());
            formLayout.add(controls);
            formLayout.setMaxWidth("400px");
            add(formLayout);
        }

        void edit(SystemUserDto systemUserDto) {
            setOpened(true);
            binder.setBean(systemUserDto);
        }

    }

}
