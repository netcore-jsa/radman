package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.dto.LoadingResult;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupFilter;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.notification.LoadingResultNotification;

import java.util.Objects;
import java.util.Optional;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: User groups")
@Route(value = "user_groups", layout = MainTemplate.class)
public class UserGroupsView extends VerticalLayout {

    private final RadiusGroupFilter filter = new RadiusGroupFilter();
    private final RadiusUserService service;
    private final SecurityService securityService;

    public UserGroupsView(RadiusUserService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        Grid<RadiusGroupDto> grid = new Grid<>(RadiusGroupDto.class, false);
        grid.addColumns("name", "description");
        DataProvider<RadiusGroupDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> service.pageRadiusUsersGroup(filter, pageable),
                value -> service.countRadiusUsersGroup(filter))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setDataProvider(dataProvider);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        UserGroupCreationDialog creationDialog = new UserGroupCreationDialog(service,
                (source, bean) -> grid.getDataProvider().refreshAll());
        UserGroupEditDialog editDialog = new UserGroupEditDialog(service,
                (source, bean) -> grid.getDataProvider().refreshItem(bean));

        Checkbox removeFromRadius = new Checkbox("Remove from Radius");
        ConfirmationDialog deleteDialog = new ConfirmationDialog("400px");
        deleteDialog.setTitle("Delete Radius group");
        deleteDialog.setContent(removeFromRadius, new Label("Are you sure?"));
        deleteDialog.setConfirmButtonCaption("Delete");
        deleteDialog.setConfirmListener(() -> {
            Optional<RadiusGroupDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(radiusGroupDto -> {
                try {
                    service.deleteRadiusUsersGroup(radiusGroupDto, removeFromRadius.getValue());
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to delete users group. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            });
            deleteDialog.setOpened(false);
        });
        deleteDialog.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                removeFromRadius.setValue(false);
            }
        });

        Button createBtn = new Button("Create", event -> creationDialog.startCreation());
        createBtn.setEnabled(role == RoleDto.ADMIN);
        Button editBtn = new Button("Edit", event -> {
            RadiusGroupDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                editDialog.edit(dto);
                deleteDialog.setOpened(false);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> {
            Optional<RadiusGroupDto> optional = grid.getSelectionModel().getFirstSelectedItem();
            optional.ifPresent(radiusGroupDto -> {
                deleteDialog.setContent(removeFromRadius, new Label("Are you sure you want to delete '" +
                        radiusGroupDto.getName() + "' user group?"));
                deleteDialog.setOpened(true);
            });
        });
        deleteBtn.setEnabled(false);
        Button loadUserGroups = new Button("Load from Radius", event -> {
            LoadingResult result = service.loadRadiusGroupsFromRadiusDB();
            LoadingResultNotification.show("Groups load result", result);
            grid.getDataProvider().refreshAll();
        });
        loadUserGroups.setEnabled(role == RoleDto.ADMIN);

        grid.asSingleSelect().addValueChangeListener(event -> {
            editBtn.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN);
            deleteBtn.setEnabled(Objects.nonNull(event.getValue()) && role == RoleDto.ADMIN);
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
        horizontalLayout.add(new H3("Users"));
        horizontalLayout.add(createBtn);
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(loadUserGroups);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

    static abstract class UserGroupFormDialog extends Dialog {

        final RadiusUserService service;
        final Binder<RadiusGroupDto> binder;

        UserGroupFormDialog(RadiusUserService service) {
            this.service = service;

            TextField username = new TextField("Name");
            username.setValueChangeMode(ValueChangeMode.EAGER);
            TextField description = new TextField("Description");
            description.setValueChangeMode(ValueChangeMode.EAGER);

            binder = new BeanValidationBinder<>(RadiusGroupDto.class);
            binder.bind(username, "name");
            binder.bind(description, "description");

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
            controlsLayout.add(getConfirmBtn());
            controlsLayout.setWidthFull();

            add(new H3(getDialogTitle()));
            add(new FormLayout(username, description));
            add(new Hr());
            add(controlsLayout);
        }

        abstract String getDialogTitle();

        abstract Button getConfirmBtn();

    }

    static class UserGroupCreationDialog extends UserGroupFormDialog {

        private final CreationListener<RadiusGroupDto> creationListener;

        UserGroupCreationDialog(RadiusUserService service,
                                CreationListener<RadiusGroupDto> creationListener) {
            super(service);
            this.creationListener = creationListener;
        }

        @Override
        String getDialogTitle() {
            return "Create Radius group";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Create", event -> {
                RadiusGroupDto dto = new RadiusGroupDto();
                if (binder.writeBeanIfValid(dto)) {
                    try {
                        dto = service.createRadiusUsersGroup(dto);
                        creationListener.onCreated(this, dto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to create radius group. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void startCreation() {
            binder.readBean(new RadiusGroupDto());
            setOpened(true);
        }

    }

    static class UserGroupEditDialog extends UserGroupFormDialog {

        private final UpdateListener<RadiusGroupDto> updateListener;

        UserGroupEditDialog(RadiusUserService service,
                            UpdateListener<RadiusGroupDto> updateListener) {
            super(service);
            this.updateListener = updateListener;
        }

        @Override
        String getDialogTitle() {
            return "Edit Radius group";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Save", event -> {
                BinderValidationStatus<RadiusGroupDto> validationStatus = binder.validate();
                if (validationStatus.isOk()) {
                    try {
                        RadiusGroupDto dto = binder.getBean();
                        dto = service.updateRadiusUsersGroup(dto);
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

        void edit(RadiusGroupDto dto) {
            binder.setBean(dto);
            setOpened(true);
        }

    }

}
