package software.netcore.radman.ui.view.nas.widget;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.ui.converter.DoubleToIntegerConverter;

public class NasForm extends FormLayout {

    @Getter
    final Binder<NasDto> binder;

    public NasForm() {
        TextField name = new TextField("Name");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        TextField shortName = new TextField("Short name");
        shortName.setValueChangeMode(ValueChangeMode.EAGER);
        TextField type = new TextField("Type");
        type.setValueChangeMode(ValueChangeMode.EAGER);
        NumberField port = new NumberField("Port");
        port.setValueChangeMode(ValueChangeMode.EAGER);
        PasswordField secret = new PasswordField("Secret");
        secret.setValueChangeMode(ValueChangeMode.EAGER);
        TextField server = new TextField("Server");
        server.setValueChangeMode(ValueChangeMode.EAGER);
        TextField community = new TextField("Community");
        community.setValueChangeMode(ValueChangeMode.EAGER);
        TextArea description = new TextArea("Description");
        description.setValueChangeMode(ValueChangeMode.EAGER);

        setWidthFull();
        setMaxWidth("700px");
        add(name, shortName, server, port, secret, type, community, description);
        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("450px", 2));

        binder = new BeanValidationBinder<>(NasDto.class);
        binder.bind(name, "nasName");
        binder.bind(shortName, "shortName");
        binder.bind(type, "type");
        binder.forField(port)
                .withConverter(new DoubleToIntegerConverter("Port must be number " +
                        "between 1 and " + 65535 + "."))
                .bind("ports");
        binder.bind(secret, "secret");
        binder.bind(server, "server");
        binder.bind(community, "community");
        binder.bind(description, "description");
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public NasDto getBean() {
        return binder.getBean();
    }

    public void setBean(NasDto nasDto) {
        binder.setBean(nasDto);
    }

}
