package software.netcore.radman.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("Radman: User groups")
@Route(value = "user_groups", layout = MainTemplate.class)
public class UserGroupsView extends Div {

    public UserGroupsView() {
        add(new Label("User groups view"));
    }

}
