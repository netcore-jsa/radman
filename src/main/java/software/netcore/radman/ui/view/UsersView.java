package software.netcore.radman.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: Users")
@Route(value = "users", layout = MainTemplate.class)
public class UsersView extends Div {

    public UsersView() {
        add(new Label("Users view"));
    }

}
