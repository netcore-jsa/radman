package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserFilter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserToGroupDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.user_groups.widget.UserGroupsForm;

import java.util.*;

public class UserGroupStep implements WizardStep<NewEntityWizardDataStorage> {

    private final RadiusUserService userService;
    private final SecurityService securityService;

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;
    private final UserGroupsForm userGroupsForm;

    public UserGroupStep(RadiusUserService userService,
                         SecurityService securityService,
                         List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.userService = userService;
        this.securityService = securityService;
        this.steps = steps;
        userGroupsForm = new UserGroupsForm();
        userGroupsForm.setBean(new RadiusGroupDto());
        contentLayout.withComponent(userGroupsForm);
    }

    @Override
    public Component getContent() {
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return userGroupsForm.isValid();
    }

    @Override
    public void onTransition() {
        steps.add(new UserGroupStepSecond(securityService, userGroupsForm.getBean().getName()));
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        dataStorage.setRadiusGroupDto(userGroupsForm.getBean());
    }

    private class UserGroupStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final Grid<RadiusUserToGroupDto> grid;

        private List<RadiusUserToGroupDto> radiusUserToGroupDtoList = new ArrayList<>();

        UserGroupStepSecond(SecurityService securityService, String groupName) {
            RoleDto role = securityService.getLoggedUserRole();
            grid = new Grid<>(RadiusUserToGroupDto.class, false);
            grid.addColumns("username", "userInRadman");
            grid.setSortableColumns("username");
            grid.setColumnReorderingAllowed(true);
            grid.setMinHeight("200px");
            grid.setHeight("100%");

            InMemoryDataProvider<RadiusUserToGroupDto> dataProvider = new ListDataProvider<>(radiusUserToGroupDtoList);
            grid.setDataProvider(dataProvider);

            Button addUserToGroup = new VButton("Add user to group", event -> {
                AddUserDialog addDialog = new AddUserDialog(groupName,
                        (source, bean) -> {
                            radiusUserToGroupDtoList.add(bean);
                            grid.getDataProvider().refreshAll();
                        });
                addDialog.open();
            }).withEnabled(role == RoleDto.ADMIN);

            Button removeUserFromGroup = new VButton("Remove user from group", event -> {
                Optional<RadiusUserToGroupDto> dtoOptional = grid.getSelectionModel().getFirstSelectedItem();
                dtoOptional.ifPresent(radiusUserToGroupDtoList::remove);
                grid.getDataProvider().refreshAll();
            }).withEnabled(false);

            grid.asSingleSelect().addValueChangeListener(event ->
                    removeUserFromGroup.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN));

            contentLayout.withComponent(
                    new VHorizontalLayout()
                            .withComponent(addUserToGroup)
                            .withComponent(removeUserFromGroup))
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
        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

    }

    private class AddUserDialog extends Dialog {

        AddUserDialog(String groupName, UpdateListener<RadiusUserToGroupDto> updateListener) {
            add(new H3("Add user to group"));

            ComboBox<RadiusUserDto> username = new ComboBox<>("Username");
            username.setItemLabelGenerator(RadiusUserDto::getUsername);
            username.setAutofocus(true);

            username.setDataProvider(new CallbackDataProvider<>(query ->
                    userService.pageRadiusUsers(new RadiusUserFilter(query.getFilter().orElse(null),
                            true, false), PageRequest.of(query.getOffset(),
                            query.getLimit(), new Sort(Sort.Direction.ASC, "id")))
                            .stream(),
                    query -> (int) userService.countRadiusUsers(new RadiusUserFilter(query.getFilter()
                            .orElse(null), true, false))));

            Button add = new Button("Add", event -> {
                if (StringUtils.isNotEmpty(groupName)) {
                    RadiusUserToGroupDto dto = new RadiusUserToGroupDto();
                    dto.setUsername(username.getValue().getUsername());
                    dto.setGroupName(groupName);
                    updateListener.onUpdated(this, dto);
                }
                close();
            });
            Button cancel = new Button("Cancel", event -> setOpened(false));

            add(username);
            add(new Hr());
            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.setWidthFull();
            controlsLayout.add(add);
            controlsLayout.add(cancel);
            add(controlsLayout);
        }

    }

}
