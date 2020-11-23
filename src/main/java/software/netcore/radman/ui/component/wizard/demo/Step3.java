package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;
import software.netcore.radman.ui.component.wizard.WizardStep;

/**
 * @since v. 1.0.3
 * @author daniel
 */
public class Step3 implements WizardStep<NewEntityWizardDataStorage> {

    private final VerticalLayout contentLayout = new VerticalLayout();

    public Step3() {
        contentLayout.add(new Label("This is a step 3"));
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
        //no-op
    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

}
