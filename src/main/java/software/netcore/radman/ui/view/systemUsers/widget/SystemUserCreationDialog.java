package software.netcore.radman.ui.view.systemUsers.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.notification.ErrorNotification;

/**
 * @author daniel
 * @since v. 1.0.3
 */
@Slf4j
public class SystemUserCreationDialog  extends Dialog {

    private final SystemUserForm systemUserForm;

    public SystemUserCreationDialog(SystemUserService service, CreationListener<SystemUserDto> creationListener) {
        systemUserForm = new SystemUserForm();

        Button createBtn = new Button("Create", event -> {
            SystemUserDto userDto;
            if (systemUserForm.isValid()) {
                try {
                    userDto = service.createSystemUser(systemUserForm.getBean());
                    creationListener.onCreated(this, userDto);
                    setOpened(false);
                } catch (DataIntegrityViolationException e) {
                    systemUserForm.getUsername().setInvalid(true);
                    systemUserForm.getUsername().setErrorMessage("User with the same username already exist.");
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

        add(systemUserForm);
        add(new Hr());
        add(controls);
    }

    public void startCreation() {
        SystemUserDto systemUserDto = new SystemUserDto();
        systemUserDto.setRole(RoleDto.ADMIN);
        systemUserDto.setAuthProvider(AuthProviderDto.LOCAL);
        systemUserForm.setBean(systemUserDto);
        setOpened(true);
    }

}
