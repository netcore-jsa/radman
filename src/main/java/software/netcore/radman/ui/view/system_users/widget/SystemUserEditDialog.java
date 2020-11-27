package software.netcore.radman.ui.view.system_users.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import lombok.extern.slf4j.Slf4j;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.notification.ErrorNotification;

/**
 * @author daniel
 * @since v. 1.0.3
 */
@Slf4j
public class SystemUserEditDialog extends Dialog {

    private final Binder<SystemUserDto> binder;

    public SystemUserEditDialog(SystemUserService service, UpdateListener<SystemUserDto> updateListener) {
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

    public void edit(SystemUserDto systemUserDto) {
        setOpened(true);
        binder.setBean(systemUserDto);
    }

}
