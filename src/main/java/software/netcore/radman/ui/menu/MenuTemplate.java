package software.netcore.radman.ui.menu;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.polymertemplate.EventHandler;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.component.wizard.Wizard;
import software.netcore.radman.ui.component.wizard.demo.IntroductionStep;
import software.netcore.radman.ui.component.wizard.demo.NewEntityWizardDataStorage;
import software.netcore.radman.ui.view.*;
import software.netcore.radman.ui.view.nas.NasView;
import software.netcore.radman.ui.view.systemUsers.SystemUsersView;
import software.netcore.radman.ui.view.userToGroup.UserToGroupView;

import javax.transaction.Transactional;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Push
@Tag("menu-layout")
@HtmlImport("src/MenuLayout.html")
@BodySize(height = "100vh", width = "100vw")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MenuTemplate extends PolymerTemplate<MenuTemplate.MenuTemplateModel> implements RouterLayout {

    private static final long serialVersionUID = 2660673607800096107L;

    private final static String SELECTED_CLASS_NAME = "selected";

    @Autowired
    private NasService nasService;
    @Autowired
    private RadiusUserService radiusUserService;
    @Autowired
    private SecurityService securityService;

    public interface MenuTemplateModel extends TemplateModel {

        void setVersion(String version);

    }

    @Id("page-nav-links")
    private Element linksContainer;

    public MenuTemplate(BuildProperties buildProperties) {
        getModel().setVersion(buildProperties.getVersion());

        addSeparator();
        addCategoryName("RadMan");
        addNavigation(UsersView.class, "Users");
        addNavigation(UserGroupsView.class, "User groups");
        addNavigation(AttributesView.class, "Attributes");
        addSeparator();
        addCategoryName("Radius");
        addNavigation(NasView.class, "NAS");
        addNavigation(NasGroupsView.class, "NAS groups");
        addNavigation(AuthView.class, "Auth (AA)");
        addNavigation(AccountingView.class, "Accounting");
        addNavigation(UserToGroupView.class, "User/Group");
        addSeparator();
        addCategoryName("System");
        addNavigation(SystemUsersView.class, "System users");
    }

    private void addNavigation(Class<? extends Component> navigationTarget, String name) {
        Element li = ElementFactory.createListItem();
        RouterLink routerLink = new RouterLink(name, navigationTarget);
        routerLink.getClassNames().add("button");
        linksContainer.appendChild(li.appendChild(routerLink.getElement()));
        routerLink.setHighlightCondition((r, event) -> Objects.equals(r.getHref(), event.getLocation().getPath()));
        routerLink.setHighlightAction((r, highlight) -> {
            if (highlight) {
                routerLink.getElement().getClassList().add(SELECTED_CLASS_NAME);
            } else {
                routerLink.getElement().getClassList().remove(SELECTED_CLASS_NAME);
            }
        });
    }

    private void addCategoryName(String name) {
        Element li = ElementFactory.createListItem();
        Element span = new Span(name).getElement();
        span.getClassList().add("category");
        li.appendChild(span);
        linksContainer.appendChild(li);
    }

    private void addSeparator() {
        Element li = ElementFactory.createListItem();
        Div div = new Div();
        div.getClassNames().add("separator");
        li.appendChild(div.getElement());
        linksContainer.appendChild(li);
    }

    @EventHandler
    private void logout() {
        SecurityContextHolder.clearContext();
        UI ui = UI.getCurrent();
        ui.getSession().getSession().invalidate();
        ui.getSession().close();
        ui.getPage().reload();
    }

    @EventHandler
    private void add() {
        Wizard<NewEntityWizardDataStorage> additionWizard = new Wizard<>(
                Wizard.Configuration.<NewEntityWizardDataStorage>builder()
                        .title("Addition wizard")
                        .maxWidth("500px")
                        .build(),
                this::saveNewEntityWizardData,
                new NewEntityWizardDataStorage());

        additionWizard.getSteps().add(
                new IntroductionStep(nasService,
                        radiusUserService,
                        securityService,
                        additionWizard.getSteps()));
        additionWizard.displayFirstStep();

        additionWizard.open();
    }

    @Transactional
    void saveNewEntityWizardData(NewEntityWizardDataStorage dataStorage) {
        if (Objects.nonNull(dataStorage.getNasDto())) {
            nasService.createNas(dataStorage.getNasDto());
        }

        if (Objects.nonNull(dataStorage.getNasGroupDtos())) {
            for (NasGroupDto nasGroupDto : dataStorage.getNasGroupDtos()) {
                nasService.createNasGroup(nasGroupDto);
            }
        }
    }

}
