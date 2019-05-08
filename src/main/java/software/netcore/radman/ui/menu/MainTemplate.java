package software.netcore.radman.ui.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * @since v. 1.0.0
 */
@Tag("main-layout")
@HtmlImport("src/MainLayout.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MainTemplate extends PolymerTemplate<TemplateModel> implements RouterLayout {

//    public MainTemplate() {
//        addNavigation(NasView.class, "NAS");
//        addNavigation(NasGroupsView.class, "NAS groups");
//        addNavigation(UsersView.class, "Users");
//        addNavigation(UserGroupsView.class, "User groups");
//        addNavigation(AuthView.class, "Auth (AA)");
//        addNavigation(AccountingView.class, "Accounting");
//        addNavigation(AttributesView.class, "Accounting");
//        addNavigation(SystemUsersView.class, "System users");
//    }

    private void addNavigation(Class<? extends Component> navigationTarget, String name) {
//        verticalLayout.add(new RouterLink(name, navigationTarget));
    }

}
