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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.Role;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.validator.PasswordValidator;
import software.netcore.radman.ui.validator.UsernameValidator;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: System users")
@Route(value = "system_users", layout = MainTemplate.class)
public class SystemUsersView extends Div {

    @Autowired
    public SystemUsersView(SystemUserService systemUserService) {
        Dialog userCreationDialog = new Dialog();
        Dialog userEditDialog = new Dialog();

        Button editBtn = new Button("Edit");
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete");
        deleteBtn.setEnabled(false);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("System users"));
        horizontalLayout.add(new Button("Create", event -> userCreationDialog.setOpened(true)));
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);
        add(horizontalLayout);

        Grid<SystemUserDto> grid = new Grid<>(SystemUserDto.class, false);
        grid.addColumns("username", "role", "lastLoginTime");
        DataProvider<SystemUserDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> systemUserService.pageSystemUsers(pageable),
                value -> systemUserService.countSystemUsers())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(event -> {
            editBtn.setEnabled(Objects.nonNull(event.getValue()));
            deleteBtn.setEnabled(Objects.nonNull(event.getValue()));
        });

        add(grid);

        SystemUserCreationForm creationForm = new SystemUserCreationForm(systemUserService,
                systemUserDto -> {
                    userCreationDialog.setOpened(false);
                    grid.getDataProvider().refreshAll();
                },
                () -> userCreationDialog.setOpened(false));
        userCreationDialog.addOpenedChangeListener(event -> creationForm.clear());
        userCreationDialog.add(creationForm);

        SystemUserEditForm editForm = new SystemUserEditForm(systemUserService);
        userEditDialog.add(editForm);

        editBtn.addClickListener(event -> {
            SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(user)) {
                editForm.edit(user);
                userEditDialog.setOpened(true);
            }
        });
        deleteBtn.addClickListener(event -> {

        });
    }

    @SuppressWarnings("Duplicates")
    static class SystemUserCreationForm extends FormLayout {

        @FunctionalInterface
        public interface CreationListener {

            void onCreated(SystemUserDto systemUserDto);

        }

        @FunctionalInterface
        public interface CancelListener {

            void onCancel();

        }

        private final Binder<SystemUserDto> binder;

        SystemUserCreationForm(SystemUserService systemUserService,
                               CreationListener creationListener,
                               CancelListener cancelListener) {
            add(new H3("New system user"));

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
                        systemUserService.createSystemUser(userDto);
                        creationListener.onCreated(userDto);
                    } catch (DataIntegrityViolationException e) {
                        username.setInvalid(true);
                        username.setErrorMessage("User with same username already exist.");
                    } catch (Exception e) {
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
            Button cancelBtn = new Button("Cancel", event -> {
                clear();
                cancelListener.onCancel();
            });
            HorizontalLayout formControls = new HorizontalLayout();
            formControls.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            formControls.add(cancelBtn, createBtn);
            formControls.setWidthFull();

            add(username);
            add(password);
            add(role);
            add(formControls);
            setMaxWidth("400px");
        }

        void clear() {
            SystemUserDto systemUserDto = new SystemUserDto();
            systemUserDto.setRole(Role.ADMIN);
            binder.readBean(systemUserDto);
        }

    }

    @SuppressWarnings("Duplicates")
    static class SystemUserEditForm extends FormLayout {

        @FunctionalInterface
        interface CancelListener {

            void onCancel();

        }

        @FunctionalInterface
        interface SavedListener {

            void onSaved(SystemUserDto systemUserDto);

        }

        private final Binder<SystemUserDto> binder;

        SystemUserEditForm(SystemUserService systemUserService) {
            add(new H3("Edit system user"));

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

            Button saveBtn = new Button("Save");
            Button cancelBtn = new Button("Cancel");

            HorizontalLayout formControls = new HorizontalLayout();
            formControls.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            formControls.add(cancelBtn, saveBtn);
            formControls.setWidthFull();

            add(role);
            add(formControls);
            setMaxWidth("400px");
        }

        void edit(SystemUserDto systemUserDto) {
            binder.readBean(systemUserDto);
        }

    }

}
