package software.netcore.radman.ui.view.attributes.widget;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Getter;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;

public class AttributeForm<T extends AttributeDto> extends FormLayout {

    private static final String SENSITIVE_DATA_WARNING_MESSAGE = "" +
            "WARNING: " +
            "Creating an attribute as \"Sensitive\" - " +
            "this can not be changed later. " +
            "If you want to see the attribute value, " +
            "you will have to delete and re-create this attribute.";

    private final Class<T> clazz;
    private final Binder<T> binder;
    @Getter
    private final TextField name;

    public AttributeForm(Class<T> clazz) {
        this.clazz = clazz;
        name = new TextField("Name");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.setWidthFull();
        TextArea description = new TextArea("Description");
        description.setValueChangeMode(ValueChangeMode.EAGER);
        description.setWidthFull();
        Checkbox sensitive = new Checkbox("Sensitive");
        sensitive.setWidthFull();
        Span sensitiveDataWarningMessage = new Span(SENSITIVE_DATA_WARNING_MESSAGE);
        sensitiveDataWarningMessage.getElement().getStyle().set("font-weight", "500");
        sensitiveDataWarningMessage.getElement().getStyle().set("word-break", "break-word");
        Div sensitiveDataWarningContainer = new Div();
        sensitiveDataWarningContainer.setWidthFull();
        sensitiveDataWarningContainer.add(new Hr());
        sensitiveDataWarningContainer.add(sensitiveDataWarningMessage);

        binder = new BeanValidationBinder<>(clazz);
        binder.bind(name, "name");
        binder.bind(description, "description");
        binder.bind(sensitive, "sensitiveData");

        sensitive.addValueChangeListener(event -> {
            if (event.getValue()) {
                addComponentAtIndex(4, sensitiveDataWarningContainer);
            } else {
                remove(sensitiveDataWarningContainer);
            }
        });

        add(name);
        add(description);
        add(sensitive);
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public AttributeDto getBean() {
        return binder.getBean();
    }

    public void setBean(T attributeDto) {
        binder.setBean(attributeDto);
    }

}
