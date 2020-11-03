package software.netcore.radman.ui.component.wizard;

import lombok.NonNull;

/**
 * @since v. 1.0.3
 * @author daniel
 */
@FunctionalInterface
public interface WizardFinalizer<T extends DataStorage> {

    void finalizeWizard(@NonNull T dataStorage) throws WizardFinalizeException;

}
