package software.netcore.radman.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Setter;
import software.netcore.radman.ui.CancelListener;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
public class ConfirmationDialog extends Dialog {

    private static final long serialVersionUID = -6200837322504194033L;

    @FunctionalInterface
    public interface ConfirmListener {

        void onConfirm();

    }

    private final H3 title = new H3();
    private final Label description = new Label();
    private final VerticalLayout contentLayout = new VerticalLayout();
    private final Button confirmBtn = new Button("Confirm");
    private final Button cancelBtn = new Button("Cancel");

    @Setter
    private ConfirmListener confirmListener;
    @Setter
    private CancelListener cancelListener;

    public ConfirmationDialog() {
        this(null);
    }

    public ConfirmationDialog(String maxWidth) {
        contentLayout.setMargin(false);
        contentLayout.setPadding(false);
        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setWidthFull();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        controlsLayout.add(cancelBtn);
        controlsLayout.add(confirmBtn);

        FormLayout layout = new FormLayout();
        layout.setMaxWidth(maxWidth == null ? "500px" : maxWidth);
        layout.add(title);
        layout.add(contentLayout);
        layout.add(new Hr());
        layout.add(controlsLayout);
        add(layout);

        cancelBtn.addClickListener(event -> {
            if (Objects.nonNull(cancelListener)) {
                cancelListener.onCancel(this);
            } else {
                setOpened(false);
            }
        });
        confirmBtn.addClickListener(event -> {
            if (Objects.nonNull(confirmListener)) {
                confirmListener.onConfirm();
            }
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDescription(String description) {
        contentLayout.removeAll();
        contentLayout.add(description);
        this.description.setText(description);
    }

    public void setContent(Component... components) {
        contentLayout.removeAll();
        contentLayout.add(components);
    }

    public void setConfirmButtonCaption(String caption) {
        this.confirmBtn.setText(caption);
    }

    public void setCancelButtonCaption(String caption) {
        this.cancelBtn.setText(caption);
    }

}
