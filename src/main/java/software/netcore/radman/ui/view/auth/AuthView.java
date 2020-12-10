package software.netcore.radman.ui.view.auth;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.view.auth.widget.AuthFormConfiguration;
import software.netcore.radman.ui.view.auth.widget.AuthenticationGrid;
import software.netcore.radman.ui.view.auth.widget.AuthorizationGrid;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: Auth (AA)")
@Route(value = "auth", layout = MenuTemplate.class)
public class AuthView extends VerticalLayout {

    private static final long serialVersionUID = 3514394536491785870L;

    private final AuthService authService;
    private final RadiusUserService userService;
    private final AttributeService attributeService;
    private final SecurityService securityService;

    @Autowired
    public AuthView(AuthService authService, RadiusUserService userService,
                    AttributeService attributeService, SecurityService securityService) {
        this.authService = authService;
        this.userService = userService;
        this.attributeService = attributeService;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setSpacing(false);
        add(new H4("Data from Radius DB - \"radcheck\", \"radgroupcheck\", \"radreply\", \"radgroupreply\" tables"));

        AuthFormConfiguration formConfig = new AuthFormConfiguration(true, false, false, false, null);
        add(new AuthenticationGrid(authService, attributeService, userService, securityService, formConfig));
        add(new AuthorizationGrid(authService, attributeService, userService, securityService, formConfig));
    }

}
