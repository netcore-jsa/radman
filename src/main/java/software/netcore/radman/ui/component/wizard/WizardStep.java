package software.netcore.radman.ui.component.wizard;

import com.vaadin.flow.component.Component;
import lombok.NonNull;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public interface WizardStep<T extends DataStorage> {

    Component getContent();

    boolean isValid();

    void writeDataToStorage(@NonNull T dataStorage);

    default void onTransition() {

    }

    default boolean hasNextStep() {
        return true;
    }

    default boolean hasPreviousStep() {
        return true;
    }

}
