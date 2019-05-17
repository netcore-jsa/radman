package software.netcore.radman.ui.notification;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;

/**
 * @since v. 1.0.0
 */
public class ErrorNotification {

    public static void show(String title, String description) {
        Notification notification = new Notification();
        notification.setDuration(3000);
        notification.add(new H3(title));
        notification.add(new Label(description));
        notification.setPosition(Notification.Position.TOP_END);
        notification.open();
    }

}
