package software.netcore.radman.ui.component;

import com.vaadin.flow.component.html.Label;

public class AdditionWizard extends ConfirmationDialog {

    public AdditionWizard() {
        super("500px");
        setTitle("Addition wizard");
        stepOne();
    }

    private void stepOne() {
        Label label = new Label("Here is step one content.");
        setContent(label);
        setConfirmButtonCaption("Next");
        setConfirmListener(this::stepTwo);
    }

    private void stepTwo() {
        Label label = new Label("Here is step two content.");
        setContent(label);
        setConfirmButtonCaption("Next");
        setConfirmListener(this::lastStep);
    }

    private void lastStep() {
        Label label = new Label("Here is last step content.");
        setContent(label);
        setConfirmButtonCaption("Finish");
        setConfirmListener(new ConfirmListener() {
            @Override
            public void onConfirm() {
                // TODO save all entries

                close();
            }
        });
    }

}
