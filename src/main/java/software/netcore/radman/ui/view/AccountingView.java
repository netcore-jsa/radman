package software.netcore.radman.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: Accounting")
@Route(value = "tutorial", layout = MainTemplate.class)
public class AccountingView extends Div {

    public AccountingView() {
        add(new Label("Accounting view"));
    }

}
