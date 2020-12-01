package software.netcore.radman.ui.view.radius_users.widget;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;

public class RadiusUserForm extends FormLayout {

    final Binder<RadiusUserDto> binder;

    public RadiusUserForm() {
        TextField username = new TextField("Username");
        username.setValueChangeMode(ValueChangeMode.EAGER);
        TextField description = new TextField("Description");
        description.setValueChangeMode(ValueChangeMode.EAGER);

        binder = new BeanValidationBinder<>(RadiusUserDto.class);
        binder.bind(username, "username");
        binder.bind(description, "description");

        add(username);
        add(description);
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public RadiusUserDto getBean() {
        return binder.getBean();
    }

    public void setBean(RadiusUserDto radiusUserDto) {
        binder.setBean(radiusUserDto);
    }

}
