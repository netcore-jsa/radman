package software.netcore.radman.ui.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.ui.menu.MainTemplate;

import java.util.Map;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: Auth (AA)")
@Route(value = "auth", layout = MainTemplate.class)
public class AuthView extends Div {

    private final AuthService authService;

    @Autowired
    public AuthView(AuthService authService) {
        this.authService = authService;
        buildView();
    }

    private void buildView() {
        add(new H2("Authentication"));
        AuthenticationDto authenticationDto = authService.getAuthentications();
        Grid<Map<String, String>> authenticationGrid = new Grid<>();
        authenticationDto.getColumnsSpec().keySet().forEach(key
                -> authenticationGrid.addColumn((ValueProvider<Map<String, String>, Object>) map
                -> map.get(key)).setHeader(key));
        authenticationGrid.setItems(authenticationDto.getData());
        add(authenticationGrid);

        add(new H2("Authorization"));
        AuthorizationDto authorizationDto = authService.getAuthorizations();
        Grid<Map<String, String>> authorizationGrid = new Grid<>();
        authorizationDto.getColumnsSpec().keySet().forEach(key
                -> authorizationGrid.addColumn((ValueProvider<Map<String, String>, Object>) map
                -> map.get(key)).setHeader(key));
        authorizationGrid.setItems(authorizationDto.getData());
        add(authorizationGrid);
    }

}
