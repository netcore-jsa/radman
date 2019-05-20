package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
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
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.Role;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.validator.PasswordValidator;
import software.netcore.radman.ui.validator.UsernameValidator;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("Radman: System users")
@Route(value = "system_users", layout = MainTemplate.class)
public class SystemUsersView extends Div {

    private final SystemUserService systemUserService;

    @Autowired
    public SystemUsersView(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
        buildView();
    }

    @SuppressWarnings("Duplicates")
    private void buildView() {
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
        DataProvider<SystemUserDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> systemUserService.pageSystemUsers(pageable),
                value -> systemUserService.countSystemUsers())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setDataProvider(dataProvider);

        ConfirmationDialog userDeleteDialog = new ConfirmationDialog();
        userDeleteDialog.setTitle("Delete system user");
        userDeleteDialog.setConfirmButtonCaption("Confirm");
        SystemUserCreationDialog createDialog = new SystemUserCreationDialog(systemUserService,
                (source, bean) -> {
                    ((SystemUserCreationDialog) source).setOpened(false);
                    grid.getDataProvider().refreshAll();
                });
        createDialog.addOpenedChangeListener(event -> createDialog.clear());
        SystemUserEditDialog editDialog = new SystemUserEditDialog(systemUserService,
                (source, bean) -> {
                    ((SystemUserEditDialog) source).setOpened(false);
                    grid.getDataProvider().refreshItem(bean);
                });

        Button createBtn = new Button("Create", event -> createDialog.setOpened(true));
        Button editBtn = new Button("Edit", event -> editDialog.setOpened(true));
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete");
        deleteBtn.setEnabled(false);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("System users"));
        horizontalLayout.add(createBtn);
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);

        grid.asSingleSelect().addValueChangeListener(event -> {
            editBtn.setEnabled(Objects.nonNull(event.getValue()));
            deleteBtn.setEnabled(Objects.nonNull(event.getValue()));
        });

        editBtn.addClickListener(event -> {
            SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(user)) {
                editDialog.edit(user);
                editDialog.setOpened(true);
            }
        });
        deleteBtn.addClickListener(event -> {
            SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(user)) {
                userDeleteDialog.setDescription("Are you sure you want to delete '" + user.getUsername() + "' user?");
                userDeleteDialog.setOpened(true);
            }
        });
        userDeleteDialog.setConfirmListener(() -> {
            SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(user)) {
                systemUserService.deleteSystemUser(user);
                grid.getDataProvider().refreshAll();
                userDeleteDialog.setOpened(false);
            }
        });

        add(horizontalLayout);
        add(grid);
    }

    @SuppressWarnings("Duplicates")
    static class SystemUserCreationDialog extends Dialog {

        private final Binder<SystemUserDto> binder;

        SystemUserCreationDialog(SystemUserService systemUserService,
                                 CreationListener<SystemUserDto> creationListener) {
            FormLayout formLayout = new FormLayout();

            formLayout.add(new H3("New system user"));

            TextField username = new TextField("Username");
            username.setRequiredIndicatorVisible(true);
            username.setValueChangeMode(ValueChangeMode.EAGER);
            username.setWidthFull();

            PasswordField password = new PasswordField("Password");
            password.setRequiredIndicatorVisible(true);
            password.setValueChangeMode(ValueChangeMode.EAGER);
            password.setWidthFull();

            ComboBox<Role> role = new ComboBox<>("Role", Role.values());
            role.setPreventInvalidInput(true);
            role.setWidthFull();

            binder = new Binder<>(SystemUserDto.class);
            binder.forField(username)
                    .withValidator(new UsernameValidator())
                    .bind(SystemUserDto::getUsername, SystemUserDto::setUsername);
            binder.forField(password)
                    .withValidator(new PasswordValidator())
                    .bind(SystemUserDto::getPassword, SystemUserDto::setPassword);
            binder.forField(role)
                    .withValidator((Validator<Role>) (value, context) -> {
                        if (value == null) {
                            return ValidationResult.error("System user access role is required.");
                        }
                        return ValidationResult.ok();
                    })
                    .bind(SystemUserDto::getRole, SystemUserDto::setRole);

            Button createBtn = new Button("Create", event -> {
                SystemUserDto userDto = new SystemUserDto();
                if (binder.writeBeanIfValid(userDto)) {
                    try {
                        userDto = systemUserService.createSystemUser(userDto);
                        creationListener.onCreated(this, userDto);
                    } catch (DataIntegrityViolationException e) {
                        username.setInvalid(true);
                        username.setErrorMessage("User with same username already exist.");
                    } catch (Exception e) {
                        log.warn("Failed to create system user. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
            Button cancelBtn = new Button("Cancel", event -> {
                clear();
                setOpened(false);
            });
            HorizontalLayout controls = new HorizontalLayout();
            controls.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controls.add(cancelBtn, createBtn);
            controls.setWidthFull();

            formLayout.add(username);
            formLayout.add(password);
            formLayout.add(role);
            formLayout.add(controls);
            formLayout.setMaxWidth("400px");
            add(formLayout);
        }

        void clear() {
            SystemUserDto systemUserDto = new SystemUserDto();
            systemUserDto.setRole(Role.ADMIN);
            binder.readBean(systemUserDto);
        }

    }

    @SuppressWarnings("Duplicates")
    static class SystemUserEditDialog extends Dialog {

        private final Binder<SystemUserDto> binder;

        SystemUserEditDialog(SystemUserService systemUserService,
                             UpdateListener<SystemUserDto> updateListener) {
            FormLayout formLayout = new FormLayout();
            formLayout.add(new H3("Edit system user"));

            ComboBox<Role> role = new ComboBox<>("Role", Role.values());
            role.setPreventInvalidInput(true);
            role.setWidthFull();

            binder = new Binder<>(SystemUserDto.class);
            binder.forField(role)
                    .withValidator((Validator<Role>) (value, context) -> {
                        if (value == null) {
                            return ValidationResult.error("System user access role is required.");
                        }
                        return ValidationResult.ok();
                    })
                    .bind(SystemUserDto::getRole, SystemUserDto::setRole);

            Button saveBtn = new Button("Save", event -> {
                if (binder.isValid()) {
                    try {
                        SystemUserDto userDto = binder.getBean();
                        userDto = systemUserService.updateSystemUser(userDto);
                        updateListener.onUpdated(this, userDto);
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
            formLayout.add(controls);
            formLayout.setMaxWidth("400px");
            add(formLayout);
        }

        void edit(SystemUserDto systemUserDto) {
            binder.setBean(systemUserDto);
        }

    }

}
