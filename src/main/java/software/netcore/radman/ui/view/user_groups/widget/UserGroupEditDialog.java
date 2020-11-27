package software.netcore.radman.ui.view.user_groups.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.notification.ErrorNotification;

@Slf4j
public class UserGroupEditDialog extends Dialog {

    private static final String TITLE = "Edit Radius group";

    private final RadiusUserService radiusUserService;
    private final UpdateListener<RadiusGroupDto> updateListener;

    private final UserGroupsForm userGroupsForm;

    public UserGroupEditDialog(@NonNull RadiusUserService radiusUserService,
                               @NonNull UpdateListener<RadiusGroupDto> updateListener) {
        this.radiusUserService = radiusUserService;
        this.updateListener = updateListener;

        userGroupsForm = new UserGroupsForm();
        userGroupsForm.setBean(new RadiusGroupDto());

        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
        controlsLayout.add(getConfirmBtn());
        controlsLayout.setWidthFull();

        add(new H3(TITLE));
        add(userGroupsForm);
        add(new Hr());
        add(controlsLayout);
    }

    Button getConfirmBtn() {
        return new Button("Save", event -> {
            if (userGroupsForm.isValid()) {
                try {
                    RadiusGroupDto dto = radiusUserService.updateRadiusUsersGroup(userGroupsForm.getBean());
                    updateListener.onUpdated(this, dto);
                    setOpened(false);
                } catch (Exception e) {
                    log.warn("Failed to update Radius group. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            }
        });
    }

    public void edit(@NonNull RadiusGroupDto dto) {
        userGroupsForm.setBean(dto);
        setOpened(true);
    }

}
