package software.netcore.radman.ui.view.systemUsers.widget;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;

public class SystemUserForm extends FormLayout {

    private final Binder<SystemUserDto> binder = new BeanValidationBinder<>(SystemUserDto.class);

    @Getter
    private final TextField username;

    public SystemUserForm() {
        username = new TextField("Username");
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

        add(new H3("New system user"));
        add(username);
        add(password);
        add(role);
        add(authProvider);
        setMaxWidth("400px");
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public SystemUserDto getBean() {
        return binder.getBean();
    }

    public void setBean(SystemUserDto systemUserDto) {
        binder.setBean(systemUserDto);
    }

}
