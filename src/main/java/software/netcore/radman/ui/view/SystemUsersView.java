package software.netcore.radman.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@Route(value = "system_users", layout = MainTemplate.class)
public class SystemUsersView extends Div {

    public SystemUsersView() {
        add(new Label("System users view"));
    }

}
