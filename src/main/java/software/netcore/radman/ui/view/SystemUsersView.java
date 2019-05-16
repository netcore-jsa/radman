package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.Role;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.validator.PasswordValidator;
import software.netcore.radman.ui.validator.UsernameValidator;

/**
 * @since v. 1.0.0
 */
@PageTitle("Radman: System users")
@Route(value = "system_users", layout = MainTemplate.class)
public class SystemUsersView extends Div {

    @Autowired
    public SystemUsersView(SystemUserService systemUserService) {

        Dialog creationDialog = new Dialog();
        SystemAccountForm accountForm = new SystemAccountForm(systemUserDto -> {

        }, () -> creationDialog.setOpened(false));
        creationDialog.add(accountForm);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("System users"));
        horizontalLayout.add(new Button("Create", event -> creationDialog.setOpened(true)));
        horizontalLayout.add(new Button("Delete"));
        add(horizontalLayout);

        Grid<SystemUserDto> grid = new Grid<>(SystemUserDto.class, false);
        grid.addColumns("username", "role", "lastLoginTime");
        DataProvider<SystemUserDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> systemUserService.pageSystemUsers(pageable),
                value -> systemUserService.countSystemUsers())
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setDataProvider(dataProvider);
        add(grid);
    }

    public static class SystemAccountForm extends FormLayout {

        @FunctionalInterface
        public interface ConfirmListener {

            void onConfirm(SystemUserDto systemUserDto);

        }

        @FunctionalInterface
        public interface DeclineListener {

            void onDecline();

        }

        SystemAccountForm(ConfirmListener confirmListener, DeclineListener declineListener) {
            TextField username = new TextField("Username");
            username.setValueChangeMode(ValueChangeMode.EAGER);
            username.setWidthFull();

            PasswordField password = new PasswordField("Password");
            password.setValueChangeMode(ValueChangeMode.EAGER);
            password.setWidthFull();

            ComboBox<Role> role = new ComboBox<>("Role");
            role.setWidthFull();
            role.setItems(Role.values());
            role.setValue(Role.READ_ONLY);

            Binder<SystemUserDto> binder = new Binder<>(SystemUserDto.class);
            binder.forField(username)
                    .withValidator(new UsernameValidator())
                    .bind(SystemUserDto::getUsername, SystemUserDto::setPassword);
            binder.forField(password)
                    .withValidator(new PasswordValidator())
                    .bind(SystemUserDto::getPassword, SystemUserDto::setPassword);
            binder.forField(role)
                    .bind(SystemUserDto::getRole, SystemUserDto::setRole);

            Button createBtn = new Button("Create");
            Button declineBtn = new Button("Decline", event -> declineListener.onDecline());
            HorizontalLayout formControls = new HorizontalLayout();
            formControls.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            formControls.add(declineBtn, createBtn);
            formControls.setWidthFull();

            add(username);
            add(password);
            add(role);
            add(formControls);
            setMaxWidth("400px");
        }
    }

}
