package software.netcore.radman.ui.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.dom.Element;
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

    private static final long serialVersionUID = 8317537279357271016L;

    private final SecurityService securityService;

    @Id("authenticationMessage")
    private Element messageLabel;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        securityService.initiateFallbackUser();
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            messageLabel.getClassList().add("visible");
        }
    }
}
