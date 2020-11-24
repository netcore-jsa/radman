package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.component.wizard.DataStorage;
import software.netcore.radman.ui.component.wizard.WizardStep;

import java.util.List;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class IntroductionStep implements WizardStep<NewEntityWizardDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();

    private final List<WizardStep<NewEntityWizardDataStorage>> steps;

    private final RadioButtonGroup<String> radioGroup;

    private static final String NAS = "NAS";
    private static final String NAS_GROUP = "NAS Group";
    private static final String USER = "User";
    private static final String USER_GROUP = "User Group";
    private static final String ATTRIBUTE = "Attribute";

    private final NasService nasService;
    private final RadiusUserService radiusUserService;
    private final SecurityService securityService;

    public IntroductionStep(NasService nasService,
                            RadiusUserService radiusUserService,
                            SecurityService securityService,
                            List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.nasService = nasService;
        this.radiusUserService = radiusUserService;
        this.securityService = securityService;
        this.steps = steps;

        radioGroup = new RadioButtonGroup<>();
        radioGroup.setItems(NAS, NAS_GROUP, USER, USER_GROUP, ATTRIBUTE);
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        contentLayout.withComponent(new Label("This is an introduction to this wizard"))
                .withComponent(radioGroup);
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
    public boolean hasPreviousStep() {
        return false;
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        //no-op
    }

    @Override
    public void onTransition() {
        buildWizardSteps();
    }

    private void buildWizardSteps() {
        switch (radioGroup.getValue()) {
            case NAS:
                steps.add(new NasStep(nasService, steps));
                break;
            case NAS_GROUP:
                steps.add(new NasGroupStep(nasService));
                break;
            case USER:
                steps.add(new UserStep(radiusUserService, securityService, steps));
                break;
            case USER_GROUP:
                break;
            case ATTRIBUTE:
                break;
        }
    }

}
