package software.netcore.radman.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: Auth (AA)")
@Route(value = "auth", layout = MainTemplate.class)
public class AuthView extends Div {

    public AuthView() {
        add(new H2("Authentication"));



        add(new H2("Authorization"));
    }
}
