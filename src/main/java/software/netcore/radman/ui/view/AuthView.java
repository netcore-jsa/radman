package software.netcore.radman.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@Route(value = "auth", layout = MainTemplate.class)
public class AuthView extends Div {

    public AuthView() {
        add(new Label("Auth (AA) view"));
    }
}
