package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import lombok.NonNull;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupFilter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.auth.widget.AuthenticationGrid;
import software.netcore.radman.ui.view.auth.widget.AuthorizationGrid;
import software.netcore.radman.ui.view.system_users.widget.SystemUserForm;
import software.netcore.radman.ui.view.user_to_group.widget.AddUserToGroupDialog;

import java.util.List;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class UserStep implements WizardStep<NewEntityWizardDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;
    private final SystemUserForm userForm;

    private final AuthService authService;
    private final AttributeService attributeService;
    private final RadiusUserService radiusUserService;
    private final SecurityService securityService;

    public UserStep(AuthService authService,
                    AttributeService attributeService,
                    RadiusUserService radiusUserService,
                    SecurityService securityService,
                    List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.authService = authService;
        this.attributeService = attributeService;
        this.radiusUserService = radiusUserService;
        this.securityService = securityService;
        this.steps = steps;

        userForm = new SystemUserForm();
        userForm.setBean(new SystemUserDto());
        contentLayout.withComponent(userForm);
    }

    @Override
    public Component getContent() {
        steps.add(new UserStepSecond(steps));
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return userForm.isValid();
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

    }

    private class UserStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final List<WizardStep<NewEntityWizardDataStorage>> steps;

        UserStepSecond(List<WizardStep<NewEntityWizardDataStorage>> steps) {
            this.steps = steps;

            RadiusGroupFilter filter = new RadiusGroupFilter();
            RoleDto role = securityService.getLoggedUserRole();

            Grid<RadiusGroupDto> grid = new Grid<>(RadiusGroupDto.class, false);
            grid.addColumns("name", "description");
            DataProvider<RadiusGroupDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                    (pageable, o) -> radiusUserService.pageRadiusUsersGroup(filter, pageable),
                    value -> radiusUserService.countRadiusUsersGroup(filter))
                    .withDefaultSort("id", SortDirection.ASCENDING)
                    .build();
            grid.getColumns().forEach(column -> column.setResizable(true));
            grid.setDataProvider(dataProvider);
            grid.setMinHeight("200px");
            grid.setHeight("100%");

            Button addUserToGroup = new Button("Add user to group", event -> {
                AddUserToGroupDialog addDialog = new AddUserToGroupDialog(radiusUserService,
                        (source, bean) -> grid.getDataProvider().refreshAll());
                addDialog.startAdding();
            });
            addUserToGroup.setEnabled(role == RoleDto.ADMIN);

            VHorizontalLayout controls = new VHorizontalLayout()
                    .withDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE)
                    .withComponent(addUserToGroup);

            contentLayout.withComponent(controls)
                    .withComponent(grid);

        }

        @Override
        public Component getContent() {
            steps.add(new UserStepThird());
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

        }

    }

    private class UserStepThird implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();

        UserStepThird() {
            AuthenticationGrid authGrid = new AuthenticationGrid(authService, attributeService, radiusUserService, securityService);
            AuthorizationGrid autzGrid = new AuthorizationGrid(authService, attributeService, radiusUserService, securityService);

            contentLayout.withComponent(authGrid)
                    .withComponent(autzGrid);
        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

    }

}
