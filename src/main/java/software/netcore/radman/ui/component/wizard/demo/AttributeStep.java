package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import lombok.NonNull;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.ui.component.wizard.Wizard;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.attributes.widget.AttributeForm;

import java.util.List;

public class AttributeStep implements WizardStep<NewEntityWizardDataStorage> {

    private static final String AUTH_ATTR = "Authentication attribute";
    private static final String AUTZ_ATTR = "Authorization attribute";

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;

    public AttributeStep(List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.steps = steps;

        VVerticalLayout formLayout = new VVerticalLayout();

        ComboBox<String> attrType = new ComboBox<>();
        attrType.setItems(AUTH_ATTR, AUTZ_ATTR);
        attrType.addValueChangeListener(event -> {
           if (event.getValue().equals(AUTH_ATTR)) {
               formLayout.removeAll();
               AttributeForm<AuthenticationAttributeDto> authForm = new AttributeForm<>(AuthenticationAttributeDto.class);
               formLayout.add(authForm);
           } else {
               formLayout.removeAll();
               AttributeForm<AuthorizationAttributeDto> autzForm = new AttributeForm<>(AuthorizationAttributeDto.class);
               formLayout.add(autzForm);
           }
        });

        contentLayout.withComponent(attrType)
                .withComponent(formLayout);
    }

    @Override
    public Component getContent() {
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

    }

    private class AttributeStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final List<Wizard<NewEntityWizardDataStorage>> steps;

        AttributeStepSecond(List<Wizard<NewEntityWizardDataStorage>> steps) {
            this.steps = steps;
        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

        }

    }

    private class AttributeStepThird implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();

        AttributeStepThird() {

        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

        }

    }

}
