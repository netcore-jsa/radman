package software.netcore.radman.ui.view.userToGroup.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import software.netcore.radman.buisness.exception.DuplicityException;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.*;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.converter.RadiusGroupDtoToNameConverter;
import software.netcore.radman.ui.converter.RadiusUserDtoToNameConverter;
import software.netcore.radman.ui.notification.ErrorNotification;

@Slf4j
public class AddUserToGroupDialog extends Dialog {

    private final Binder<RadiusUserToGroupDto> binder;

    public AddUserToGroupDialog(RadiusUserService userService, UpdateListener<RadiusUserToGroupDto> updateListener) {
        add(new H3("Add user to group"));

        ComboBox<RadiusUserDto> username = new ComboBox<>("Username");
        username.setItemLabelGenerator(RadiusUserDto::getUsername);
        username.setAutofocus(true);
        ComboBox<RadiusGroupDto> groupName = new ComboBox<>("Group name");
        groupName.setItemLabelGenerator(RadiusGroupDto::getName);

        username.setDataProvider(new CallbackDataProvider<>(query ->
                userService.pageRadiusUsers(new RadiusUserFilter(query.getFilter().orElse(null),
                        true, false), PageRequest.of(query.getOffset(),
                        query.getLimit(), new Sort(Sort.Direction.ASC, "id")))
                        .stream(),
                query -> (int) userService.countRadiusUsers(new RadiusUserFilter(query.getFilter()
                        .orElse(null), true, false))));
        groupName.setDataProvider(new CallbackDataProvider<>(query ->
                userService.pageRadiusUsersGroup(new RadiusGroupFilter(query.getFilter().orElse(null),
                        true, false), PageRequest.of(query.getOffset(),
                        query.getLimit(), new Sort(Sort.Direction.ASC, "id")))
                        .stream(),
                query -> (int) userService.countRadiusUsersGroup(new RadiusGroupFilter(query.getFilter()
                        .orElse(null), true, false))));
        groupName.addValueChangeListener(event -> username.setInvalid(false));

        binder = new BeanValidationBinder<>(RadiusUserToGroupDto.class);
        binder.forField(username)
                .withConverter(new RadiusUserDtoToNameConverter())
                .bind("username");
        binder.forField(groupName)
                .withConverter(new RadiusGroupDtoToNameConverter())
                .bind("groupName");

        Button add = new Button("Add", event -> {
            BinderValidationStatus<RadiusUserToGroupDto> status = binder.validate();
            if (status.isOk()) {
                try {
                    RadiusUserToGroupDto dto = new RadiusUserToGroupDto();
                    binder.writeBean(dto);
                    dto = userService.addRadiusUserToGroup(dto);
                    updateListener.onUpdated(this, dto);
                    setOpened(false);
                } catch (DuplicityException e) {
                    username.setInvalid(true);
                    username.setErrorMessage("User already in the group");
                } catch (Exception e) {
                    log.warn("Failed to add radius user to a group. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                    setOpened(false);
                }
            }
        });
        Button cancel = new Button("Cancel", event -> setOpened(false));

        add(new HorizontalLayout(username, groupName));
        add(new Hr());
        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        controlsLayout.setWidthFull();
        controlsLayout.add(add);
        controlsLayout.add(cancel);
        add(controlsLayout);
    }

    public void startAdding() {
        setOpened(true);
        binder.readBean(new RadiusUserToGroupDto());
    }

}
