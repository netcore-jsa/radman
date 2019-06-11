package software.netcore.radman.ui.notification;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import software.netcore.radman.buisness.service.dto.LoadingResult;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
public class LoadingResultNotification {

    public static void show(String title, LoadingResult result) {
        Notification notification = new Notification();
        notification.setDuration(5000);
        notification.add(new H3(title));
        VerticalLayout description = new VerticalLayout();
        description.setMargin(false);
        description.setSpacing(false);
        description.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        description.add(row(label("Loaded", "100px"), label(result.getLoaded())));
        description.add(row(label("Duplicate", "100px"), label(result.getDuplicate())));
        description.add(row(label("Errored", "100px"), label(result.getErrored())));
        notification.add(description);
        notification.setPosition(Notification.Position.TOP_END);
        notification.open();
    }

    private static Label label(Object value) {
        return label(value, null);
    }

    private static Label label(Object value, String width) {
        Label label = new Label(String.valueOf(value));
        if (Objects.nonNull(width)) {
            label.setWidth(width);
        }
        return label;
    }

    private static HorizontalLayout row(Component... components) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        layout.add(components);
        return layout;
    }

}
