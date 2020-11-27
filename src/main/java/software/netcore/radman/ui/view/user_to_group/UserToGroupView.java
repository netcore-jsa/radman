package software.netcore.radman.ui.view.user_to_group;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.*;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;
import software.netcore.radman.ui.view.user_to_group.widget.AddUserToGroupDialog;

import java.util.Objects;
import java.util.Optional;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: Users/Groups")
@Route(value = "user_to_group", layout = MenuTemplate.class)
public class UserToGroupView extends VerticalLayout {

    private static final long serialVersionUID = -7023677674319412929L;

    private final Filter filter = new Filter();
    private final RadiusUserService userService;
    private final SecurityService securityService;

    public UserToGroupView(RadiusUserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        Grid<RadiusUserToGroupDto> grid = new Grid<>(RadiusUserToGroupDto.class, false);
        grid.addColumns("username", "groupName", "userInRadman", "groupInRadman");
        DataProvider<RadiusUserToGroupDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> userService.pageRadiusUserToGroupRecords(filter, pageable),
                value -> userService.countRadiusUserToGroupRecords(filter))
                .withDefaultSort("username", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.setSortableColumns("username", "groupName");
        grid.setColumnReorderingAllowed(true);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        Button addUserToGroup = new Button("Add user to group", event -> {
            AddUserToGroupDialog addDialog = new AddUserToGroupDialog(userService,
                    (source, bean) -> grid.getDataProvider().refreshAll());
            addDialog.startAdding();
        });
        addUserToGroup.setEnabled(role == RoleDto.ADMIN);

        ConfirmationDialog deleteDialog = new ConfirmationDialog();
        deleteDialog.setTitle("Delete User to Group mapping");
        deleteDialog.setDescription("Are you sure?");
        deleteDialog.setConfirmButtonCaption("Delete");
        deleteDialog.setConfirmListener(() -> {
            Optional<RadiusUserToGroupDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            try {
                optional.ifPresent(userService::removeRadiusUserFromGroup);
                grid.getDataProvider().refreshAll();
            } catch (Exception e) {
                log.warn("Failed to delete user to group mapping. Reason = '{}'", e.getMessage());
                ErrorNotification.show("Error",
                        "Ooops, something went wrong, try again please");
            }
            deleteDialog.setOpened(false);
        });
        Button removeUserFromGroup = new Button("Remove user from group", event -> deleteDialog.setOpened(true));
        removeUserFromGroup.setEnabled(false);

        grid.asSingleSelect().addValueChangeListener(event ->
                removeUserFromGroup.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN));

        TextField search = new TextField(event -> {
            filter.setSearchText(event.getValue());
            grid.getDataProvider().refreshAll();
        });
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setPlaceholder("Search...");

        add(new H4("Data from Radius DB - \"radusergroup\" table"));
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("Users to Groups"));
        horizontalLayout.add(addUserToGroup);
        horizontalLayout.add(removeUserFromGroup);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

}
