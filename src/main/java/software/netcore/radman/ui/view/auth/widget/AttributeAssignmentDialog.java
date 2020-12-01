package software.netcore.radman.ui.view.auth.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.buisness.service.auth.dto.AuthDto;
import software.netcore.radman.buisness.service.auth.dto.AuthTarget;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.notification.ErrorNotification;

@Slf4j
public abstract class AttributeAssignmentDialog<T extends AuthDto, U extends AttributeDto> extends Dialog {

    private final RadiusUserService userService;
    private final CreationListener<Void> creationListener;

    private AuthForm<T, U> authForm;

    public AttributeAssignmentDialog(RadiusUserService userService,
                                     CreationListener<Void> creationListener) {
        this.userService = userService;
        this.creationListener = creationListener;
    }

    private void build() {
        removeAll();

        authForm = new AuthForm<>(getClazz(), userService, false, false, null,
                (searchText, offset, limit) ->
                        pageAttributes(new AttributeFilter(searchText, true, false),
                                PageRequest.of(offset, limit, new Sort(Sort.Direction.ASC, "id"))),
                filter ->
                        (int) countAttributes(filter));

        Button assignBtn = new Button("Assign", event -> {
            T dto = getNewBeanInstance();
            if (authForm.isValid()) {
                try {
                    assignAuth(dto);
                    creationListener.onCreated(this, null);
                    setOpened(false);
                } catch (Exception e) {
                    log.warn("Failed to assign auth. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            }
        });
        Button cancelBtn = new Button("Cancel", event -> setOpened(false));

        add(new H3(getDialogTitle()));
        add(authForm);
        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        controlsLayout.setWidthFull();
        controlsLayout.add(cancelBtn);
        controlsLayout.add(assignBtn);
        add(new Hr());
        add(controlsLayout);
    }

    abstract String getDialogTitle();

    abstract Class<T> getClazz();

    abstract T getNewBeanInstance();

    abstract long countAttributes(AttributeFilter filter);

    abstract Page<U> pageAttributes(AttributeFilter filter, Pageable pageable);

    abstract void assignAuth(T authDto);

    public void startAssigment() {
        build();
        setOpened(true);
        T auth = getNewBeanInstance();
        auth.setAuthTarget(AuthTarget.RADIUS_USER);
        authForm.setBean(auth);
    }

}
