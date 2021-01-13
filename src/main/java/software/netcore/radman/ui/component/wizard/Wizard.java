package software.netcore.radman.ui.component.wizard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public final class Wizard<T extends DataStorage> extends Dialog {

    @Getter
    @Builder
    public static class Configuration {

        @NonNull
        private final String title;
        private final String maxWidth;

    }

    private final VerticalLayout contentHolder = new VerticalLayout();
    private final Button next = new Button();
    private final Button previous = new Button("Previous");

    @Getter
    private final List<WizardStep<T>> steps = new LinkedList<>(); //TODO change to map

    @Getter
    private final Configuration configuration;
    private final WizardFinalizer<T> wizardFinalizer;
    private final T dataStorage;
    private int position = 0;
    boolean invalid = false;

    public Wizard(@NonNull Configuration configuration,
                  @NonNull WizardFinalizer<T> wizardFinalizer,
                  @NonNull T dataStorage) {
        this.configuration = configuration;
        this.wizardFinalizer = wizardFinalizer;
        this.dataStorage = dataStorage;

        add(contentHolder);

        Button cancel = new Button("Cancel");
        cancel.addClickListener(event -> setOpened(false));
        previous.addClickListener(event -> handleTransitionToPrevious());
        next.addClickListener(event -> handleTransitionToNext());

        HorizontalLayout controls = new HorizontalLayout();
        controls.add(cancel);
        controls.add(previous);
        controls.add(next);

        FormLayout layout = new FormLayout();
        layout.setMaxWidth(configuration.getMaxWidth() == null ? "500px" : configuration.getMaxWidth());
        layout.add(new H3(configuration.getTitle()));
        layout.add(contentHolder);
        layout.add(new Hr());
        layout.add(controls);
        add(layout);
    }

    public void displayFirstStep() {
        position = 0;
        contentHolder.add(getSteps().get(position).getContent());
        updateNextButton();
        updatePreviousButton();
    }

    private void handleTransitionToNext() {
//        if (position + 1 < getSteps().size()) {
        if (getStep().hasNextStep()) {
            if (getStep().isValid()) {
                getStep().onTransition();
                WizardStep<? extends DataStorage> nextStepCandidate = getSteps().get(position + 1);
                if (Objects.nonNull(nextStepCandidate)) {

//                    nextStepCandidate.onTransition();

                    contentHolder.removeAll();
                    contentHolder.add(nextStepCandidate.getContent());
                    position++;

                    updateNextButton();
                    updatePreviousButton();
                }
            }
        } else {
            getSteps().forEach(step -> {
                if (step.isValid()) {
                    step.writeDataToStorage(dataStorage);
                }
                if (!step.hasNextStep() && step.isValid()) {
                    try {
                        wizardFinalizer.finalizeWizard(dataStorage);
                        setOpened(false);
                    } catch (WizardFinalizeException e) {
                        contentHolder.removeAll();
                        contentHolder.add(new Label(e.getMessage()));

                        Button backToWizard = new Button("Back to wizard");
                        backToWizard.addClickListener(buttonClickEvent -> {
                            contentHolder.removeAll();
                            contentHolder.add(getSteps().get(position).getContent());
                        });
                        contentHolder.add(backToWizard);
                    }
                }
            });
        }
    }

    private void handleTransitionToPrevious() {
        if (position > 0) {
            WizardStep<? extends DataStorage> nextStepCandidate = getSteps().get(position - 1);
            if (Objects.nonNull(nextStepCandidate)) {
                contentHolder.removeAll();
                contentHolder.add(nextStepCandidate.getContent());
                position--;

                updateNextButton();
                updatePreviousButton();
            }
        }
    }

    private void updateNextButton() {
        next.setText(getStep().hasNextStep() ? "Next" : "Finish");
    }

    private void updatePreviousButton() {
        previous.setEnabled(getStep().hasPreviousStep());
    }

    private WizardStep<T> getStep() {
        return getSteps().get(position);
    }

}
