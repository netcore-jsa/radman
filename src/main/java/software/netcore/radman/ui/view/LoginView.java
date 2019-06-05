package software.netcore.radman.ui.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import lombok.RequiredArgsConstructor;
import software.netcore.radman.buisness.service.security.SecurityService;

@Tag("login-view")
@PageTitle("Login")
@Route(value = "login")
@RequiredArgsConstructor
@HtmlImport("src/LoginView.html")
public class LoginView extends PolymerTemplate<TemplateModel> implements BeforeEnterObserver {

    private final SecurityService securityService;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        System.out.println(hashCode());
        securityService.initiateFallbackUser();
    }

}
