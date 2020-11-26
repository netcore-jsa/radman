package software.netcore.radman.ui.view.userGroups.widget;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;

public class UserGroupsForm extends FormLayout {

    private final Binder<RadiusGroupDto> binder;

    public UserGroupsForm() {
        TextField username = new TextField("Name");
        username.setValueChangeMode(ValueChangeMode.EAGER);
        TextField description = new TextField("Description");
        description.setValueChangeMode(ValueChangeMode.EAGER);

        binder = new BeanValidationBinder<>(RadiusGroupDto.class);
        binder.bind(username, "name");
        binder.bind(description, "description");

        add(username, description);
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public RadiusGroupDto getBean() {
        return binder.getBean();
    }

    public void setBean(RadiusGroupDto radiusGroupDto) {
        binder.setBean(radiusGroupDto);
    }

}
