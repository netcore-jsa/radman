package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.NonNull;
import software.netcore.radman.ui.component.wizard.WizardStep;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class Step2 implements WizardStep<DemoDataStorage> {

    private final VerticalLayout contentLayout = new VerticalLayout();

    public Step2() {
        contentLayout.add(new Label("This is a step 2"));
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
    public void writeDataToStorage(@NonNull DemoDataStorage dataStorage) {
        //no-op
    }

}
