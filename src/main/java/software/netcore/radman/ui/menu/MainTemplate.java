package software.netcore.radman.ui.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.templatemodel.TemplateModel;
import software.netcore.radman.ui.view.*;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Push
@Tag("main-layout")
@HtmlImport("src/MainLayout.html")
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MainTemplate extends PolymerTemplate<TemplateModel> implements RouterLayout {

    private final static String SELECTED_CLASS_NAME = "selected";

    @Id("page-nav-links")
    private Element linksContainer;

    public MainTemplate() {
        addNavigation(NasView.class, "NAS");
        addNavigation(NasGroupsView.class, "NAS groups");
        addNavigation(UsersView.class, "Users");
        addNavigation(UserGroupsView.class, "User groups");
        addNavigation(AuthView.class, "Auth (AA)");
        addNavigation(AccountingView.class, "Accounting");
        addNavigation(AttributesView.class, "Attributes");
        addNavigation(SystemUsersView.class, "System users");
    }

    private void addNavigation(Class<? extends Component> navigationTarget, String name) {
        Element li = ElementFactory.createListItem();
        RouterLink routerLink = new RouterLink(name, navigationTarget);
        linksContainer.appendChild(li.appendChild(routerLink.getElement()));
        routerLink.setHighlightCondition((r, event) -> Objects.equals(r.getHref(), event.getLocation().getPath()));
        routerLink.setHighlightAction((r, highlight) -> {
            if (highlight) {
                li.getClassList().add(SELECTED_CLASS_NAME);
            } else {
                li.getClassList().remove(SELECTED_CLASS_NAME);
            }
        });
    }

}
