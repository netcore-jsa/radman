package software.netcore.radman.ui.view.systemUsers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.system.SystemUserService;
import software.netcore.radman.buisness.service.user.system.dto.AuthProviderDto;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.buisness.service.user.system.dto.SystemUserDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;
import software.netcore.radman.ui.view.systemUsers.widget.SystemUserCreationDialog;
import software.netcore.radman.ui.view.systemUsers.widget.SystemUserEditDialog;
import software.netcore.radman.ui.view.systemUsers.widget.SystemUserForm;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: System users")
@Route(value = "system_users", layout = MenuTemplate.class)
public class SystemUsersView extends VerticalLayout {

    private static final long serialVersionUID = 7080212781595068347L;

    private final Filter filter = new Filter();
    private final SystemUserService service;
    private final SecurityService securityService;

    @Autowired
    public SystemUsersView(SystemUserService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
        buildView();
    }

    @SuppressWarnings("Duplicates")
    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        if (role == RoleDto.READ_ONLY) {
            add(new H2("Sorry, you don't have access to this view"));
        } else {
            Grid<SystemUserDto> grid = new Grid<>(SystemUserDto.class, false);
            grid.addColumns("username", "role");
            grid.addColumn(new LocalDateTimeRenderer<>(systemUserDto -> {
                if (systemUserDto.getLastLoginTime() == null) {
                    return null;
                }
                return LocalDateTime.ofEpochSecond(systemUserDto.getLastLoginTime(), 0,
                        OffsetDateTime.now().getOffset());
            }, DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm, a", Locale.US), "never"))
                    .setSortable(true)
                    .setHeader("Last login time")
                    .setSortProperty("lastLoginTime");
            grid.addColumn("authProvider").setHeader("Authentication provider");
            DataProvider<SystemUserDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                    (pageable, o) -> service.pageSystemUsers(filter.getSearchText(), pageable),
                    value -> service.countSystemUsers(filter.getSearchText()))
                    .withDefaultSort("id", SortDirection.ASCENDING)
                    .build();
            grid.getColumns().forEach(column -> column.setResizable(true));
            grid.setColumnReorderingAllowed(true);
            grid.setDataProvider(dataProvider);
            grid.setMinHeight("500px");
            grid.setHeight("100%");

            SystemUserCreationDialog createDialog = new SystemUserCreationDialog(service,
                    (source, bean) -> grid.getDataProvider().refreshAll());
            SystemUserEditDialog editDialog = new SystemUserEditDialog(service,
                    (source, bean) -> grid.getDataProvider().refreshItem(bean));
            ConfirmationDialog deleteDialog = new ConfirmationDialog();
            deleteDialog.setTitle("Delete system user");
            deleteDialog.setConfirmButtonCaption("Confirm");
            deleteDialog.setConfirmListener(() -> {
                SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(user)) {
                    service.deleteSystemUser(user);
                    grid.getDataProvider().refreshAll();
                    deleteDialog.setOpened(false);
                }
            });

            Button createBtn = new Button("Create", event -> createDialog.startCreation());
            Button editBtn = new Button("Edit", event -> {
                SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(user)) {
                    editDialog.edit(user);
                }
            });
            editBtn.setEnabled(false);
            Button deleteBtn = new Button("Delete", event -> {
                SystemUserDto user = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(user)) {
                    deleteDialog.setDescription("Are you sure you want to delete '" + user.getUsername() + "' user?");
                    deleteDialog.setOpened(true);
                }
            });
            deleteBtn.setEnabled(false);

            grid.asSingleSelect().addValueChangeListener(event -> {
                editBtn.setEnabled(Objects.nonNull(event.getValue()));
                deleteBtn.setEnabled(Objects.nonNull(event.getValue()));
            });

            TextField search = new TextField(event -> {
                filter.setSearchText(event.getValue());
                grid.getDataProvider().refreshAll();
            });
            search.setValueChangeMode(ValueChangeMode.EAGER);
            search.setPlaceholder("Search...");

            add(new H4("Data from RadMan DB"));
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            horizontalLayout.add(new H3("System users"));
            horizontalLayout.add(createBtn);
            horizontalLayout.add(editBtn);
            horizontalLayout.add(deleteBtn);
            horizontalLayout.add(search);
            add(horizontalLayout);
            add(grid);
        }
    }

}
