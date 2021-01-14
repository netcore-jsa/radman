package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.NonNull;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserToGroupDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.auth.widget.AuthFormConfiguration;
import software.netcore.radman.ui.view.auth.widget.AuthenticationGrid;
import software.netcore.radman.ui.view.auth.widget.AuthorizationGrid;
import software.netcore.radman.ui.view.radius_users.widget.RadiusUserForm;
import software.netcore.radman.ui.view.user_groups.widget.UserGroupCreationDialog;
import software.netcore.radman.ui.view.user_to_group.widget.AddUserToGroupDialog;

import java.util.*;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class UserStep implements WizardStep<NewEntityWizardDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;
    private final RadiusUserForm userForm;

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

        userForm = new RadiusUserForm();
        userForm.setBean(new RadiusUserDto());
        contentLayout.withComponent(new Label("User - Lorem ipsum"))
                .withComponent(userForm);
    }

    @Override
    public Component getContent() {
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return userForm.isValid();
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        dataStorage.setRadiusUserDto(userForm.getBean());
    }

    @Override
    public void onTransition() {
        steps.add(new UserStepSecond(userForm.getBean(), steps));
    }

    private class UserStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final List<WizardStep<NewEntityWizardDataStorage>> steps;

        private final Set<RadiusGroupDto> radiusGroupDtoSet = new HashSet<>();
        private final List<RadiusUserToGroupDto> radiusUserToGroupDtoList;

        UserStepSecond(RadiusUserDto radiusUserDto, List<WizardStep<NewEntityWizardDataStorage>> steps) {
            this.steps = steps;

            RoleDto role = securityService.getLoggedUserRole();

            Grid<RadiusUserToGroupDto> grid = new Grid<>(RadiusUserToGroupDto.class, false);
            grid.addColumns("groupName");
            radiusUserToGroupDtoList = new ArrayList<>();
            InMemoryDataProvider<RadiusUserToGroupDto> dataProvider = new ListDataProvider<>(radiusUserToGroupDtoList);
            grid.getColumns().forEach(column -> column.setResizable(true));
            grid.setDataProvider(dataProvider);
            grid.setMinHeight("200px");
            grid.setHeight("100%");

            Button addUserToGroup = new Button("Add user to group", event -> {
                AddUserToGroupDialog addDialog = new AddUserToGroupDialog(radiusUserService,
                        (source, bean) -> {
                            radiusUserToGroupDtoList.add(bean);
                            grid.getDataProvider().refreshAll();
                        });
                addDialog.updateUsernameDataProvider(new ListDataProvider<>(Collections.singletonList(radiusUserDto)));
                addDialog.open();
            });
            addUserToGroup.setEnabled(role == RoleDto.ADMIN);

            Button createGroup = new Button("Create group", event -> {
                UserGroupCreationDialog creationDialog = new UserGroupCreationDialog(radiusUserService,
                        (source, bean) -> {
                            radiusGroupDtoSet.add(bean);
                            RadiusUserToGroupDto radiusUserToGroupDto = new RadiusUserToGroupDto();
                            radiusUserToGroupDto.setUsername(radiusUserDto.getUsername());
                            radiusUserToGroupDto.setGroupName(bean.getName());
                            radiusUserToGroupDtoList.add(radiusUserToGroupDto);
                            grid.getDataProvider().refreshAll();
                        });
                creationDialog.open();
            });

            VHorizontalLayout controls = new VHorizontalLayout()
                    .withDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE)
                    .withComponent(addUserToGroup)
                    .withComponent(createGroup);

            contentLayout.withComponent(new Label("User's groups"))
                    .withComponent(controls)
                    .withComponent(grid);
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
            dataStorage.setRadiusUserToGroupDtos(new HashSet<>(radiusUserToGroupDtoList));
            dataStorage.setRadiusGroupDtos(radiusGroupDtoSet);
        }

        @Override
        public void onTransition() {
            steps.add(new UserStepThird(userForm.getBean()));
        }

    }

    private class UserStepThird implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();

        UserStepThird(RadiusUserDto radiusUserDto) {
            AuthFormConfiguration formConfig = new AuthFormConfiguration(false, true, false, false, radiusUserDto);
            AuthenticationGrid authGrid = new AuthenticationGrid(authService, attributeService, radiusUserService, securityService, formConfig);
            AuthorizationGrid autzGrid = new AuthorizationGrid(authService, attributeService, radiusUserService, securityService, formConfig);
            authGrid.setMaxHeight("250px");
            autzGrid.setMaxHeight("250px");
            authGrid.getStyle().set("overflow", "auto");
            autzGrid.getStyle().set("overflow", "auto");

            contentLayout.withComponent(new Label("Attributes"))
                    .withComponent(authGrid)
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
            //no-op
        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

    }

}
