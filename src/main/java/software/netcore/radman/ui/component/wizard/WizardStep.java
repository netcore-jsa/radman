package software.netcore.radman.ui.component.wizard;

import com.vaadin.flow.component.Component;
import lombok.NonNull;

/**
 * @since v. 1.0.3
 * @author daniel
 */
public interface WizardStep<T extends DataStorage> {

    Component getContent();

    boolean isValid();

    void writeDataToStorage(@NonNull T dataStorage);

}
