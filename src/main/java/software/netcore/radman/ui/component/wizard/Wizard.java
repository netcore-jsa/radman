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
import lombok.Singular;

import java.util.List;
import java.util.Objects;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public final class Wizard<T extends DataStorage> extends Dialog {

    @Getter
    @Builder
    public static class Configuration<T extends DataStorage> {

        @NonNull
        private final String title;
        private final String maxWidth;
        @Singular
        private final List<WizardStep<T>> steps;

    }

    private final VerticalLayout contentHolder = new VerticalLayout();
    private final Button next = new Button();
    private final Button previous = new Button("Previous");

    private final Configuration<T> configuration;
    private final WizardFinalizer<T> wizardFinalizer;
    private final T dataStorage;
    private int position = 0;

    public Wizard(@NonNull Configuration<T> configuration,
                  @NonNull WizardFinalizer<T> wizardFinalizer,
                  @NonNull T dataStorage) {
        this.configuration = configuration;
        this.wizardFinalizer = wizardFinalizer;
        this.dataStorage = dataStorage;

        if (!configuration.getSteps().isEmpty()) {
            contentHolder.add(configuration.getSteps().get(0).getContent()); // displays first step
        }
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

        updateNextButton();
        updatePreviousButton();
    }

    private void handleTransitionToNext() {
        if (position + 1 < configuration.getSteps().size()) {
            WizardStep<T> nextStepCandidate = configuration.getSteps().get(position + 1);
            if (Objects.nonNull(nextStepCandidate)) {
                if (nextStepCandidate.isValid()) {

                    contentHolder.removeAll();
                    contentHolder.add(nextStepCandidate.getContent());
                    position++;

                    updateNextButton();
                    updatePreviousButton();
                }
            }
        } else {
            try {
                wizardFinalizer.finalizeWizard(dataStorage);
                setOpened(false);
            } catch (WizardFinalizeException e) {
                contentHolder.removeAll();
                contentHolder.add(new Label(e.getMessage()));

                Button backToWizard = new Button("Back to wizard");
                backToWizard.addClickListener(buttonClickEvent -> {
                    contentHolder.removeAll();
                    contentHolder.add(configuration.getSteps().get(position).getContent());
                });
                contentHolder.add(backToWizard);
            }
        }
    }

    private void handleTransitionToPrevious() {
        if (position > 0) {
            WizardStep<T> nextStepCandidate = configuration.getSteps().get(position - 1);
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
        next.setText(isLastStep() ? "Finish" : "Next");
    }

    private void updatePreviousButton() {
        previous.setEnabled(position != 0);
    }

    private boolean isLastStep() {
        return configuration.getSteps().size() == position + 1;
    }

}
