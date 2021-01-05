package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import lombok.NonNull;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.buisness.service.auth.dto.AuthDto;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.attributes.widget.AttributeForm;
import software.netcore.radman.ui.view.auth.widget.AuthForm;
import software.netcore.radman.ui.view.auth.widget.AuthFormConfiguration;

import java.util.List;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class AttributeStep implements WizardStep<NewEntityWizardDataStorage> {

    private static final String AUTH_ATTR = "Authentication attribute";
    private static final String AUTZ_ATTR = "Authorization attribute";

    private final RadiusUserService radiusUserService;
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private ComboBox<String> attrType;
    private AttributeForm<? extends AttributeDto> attributeForm;

    public AttributeStep(RadiusUserService radiusUserService, List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.radiusUserService = radiusUserService;
        this.steps = steps;

        VVerticalLayout formLayout = new VVerticalLayout();

        attrType = new ComboBox<>();
        attrType.setItems(AUTH_ATTR, AUTZ_ATTR);
        attrType.addValueChangeListener(event -> {
            if (event.getValue().equals(AUTH_ATTR)) {
                formLayout.removeAll();
                attributeForm = new AttributeForm<>(AuthenticationAttributeDto.class);
                formLayout.add(attributeForm);
            } else {
                formLayout.removeAll();
                attributeForm = new AttributeForm<>(AuthorizationAttributeDto.class);
                formLayout.add(attributeForm);
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
        return true;
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        if (attrType.getValue().equals(AUTH_ATTR)) {
            dataStorage.setAuthenticationAttributeDto((AuthenticationAttributeDto) attributeForm.getBean());
        } else if (attrType.getValue().equals(AUTZ_ATTR)) {
            dataStorage.setAuthorizationAttributeDto((AuthorizationAttributeDto) attributeForm.getBean());
        }
    }

    @Override
    public void onTransition() {
        steps.add(new AttributeStepSecond(attrType.getValue(), attributeForm.getBean(), steps));
    }

    private class AttributeStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final String attrType;
        private final List<WizardStep<NewEntityWizardDataStorage>> steps;
        private AuthForm<? extends AuthDto, ? extends AttributeDto> authForm;

        AttributeStepSecond(String attrType, AttributeDto attribute, List<WizardStep<NewEntityWizardDataStorage>> steps) {
            this.attrType = attrType;
            this.steps = steps;
            if (attrType.equals(AUTH_ATTR)) {
                authForm = new AuthForm<AuthenticationDto, AuthenticationAttributeDto>(AuthenticationDto.class,
                        radiusUserService, new AuthFormConfiguration(false, false, true, false, null),
                        (AuthenticationAttributeDto) attribute, null, null);
//                authForm.setBean(new AuthenticationDto());
            } else if (attrType.equals(AUTZ_ATTR)) {
                authForm = new AuthForm<AuthorizationDto, AuthorizationAttributeDto>(AuthorizationDto.class,
                        radiusUserService, new AuthFormConfiguration(false, false, true, false, null),
                        (AuthorizationAttributeDto) attribute, null, null);
//                authForm.setBean(new AuthorizationDto());
            }

            contentLayout.add(authForm);
        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return authForm.isValid();
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
            if (attrType.equals(AUTH_ATTR)) {
                dataStorage.getAuthenticationDto().add((AuthenticationDto) authForm.getBean());
            } else if (attrType.equals(AUTZ_ATTR)) {
                dataStorage.getAuthorizationDto().add((AuthorizationDto) authForm.getBean());
            }
        }

        @Override
        public void onTransition() {
            steps.add(new AttributeStepThird(attrType, attributeForm.getBean()));
        }

    }

    private class AttributeStepThird implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final String attrType;
        private AuthForm<? extends AuthDto, ? extends AttributeDto> authForm;

        AttributeStepThird(String attrType, AttributeDto attribute) {
            this.attrType = attrType;
            if (attrType.equals(AUTH_ATTR)) {
                authForm = new AuthForm<AuthenticationDto, AuthenticationAttributeDto>(AuthenticationDto.class,
                        radiusUserService, new AuthFormConfiguration(false, false, false, true, null),
                        (AuthenticationAttributeDto) attribute, null, null);
            } else if (attrType.equals(AUTZ_ATTR)) {
                authForm = new AuthForm<AuthorizationDto, AuthorizationAttributeDto>(AuthorizationDto.class,
                        radiusUserService, new AuthFormConfiguration(false, false, false, true, null),
                        (AuthorizationAttributeDto) attribute, null, null);
            }

            contentLayout.add(authForm);
        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return authForm.isValid();
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
            if (attrType.equals(AUTH_ATTR)) {
                dataStorage.getAuthenticationDto().add((AuthenticationDto) authForm.getBean());
            } else if (attrType.equals(AUTZ_ATTR)) {
                dataStorage.getAuthorizationDto().add((AuthorizationDto) authForm.getBean());
            }
        }

        @Override
        public boolean hasNextStep() {
            return false;
        }
    }

}
