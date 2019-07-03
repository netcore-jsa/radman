package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.system.dto.RoleDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.converter.DoubleToIntegerConverter;
import software.netcore.radman.ui.menu.MenuTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("RadMan: NAS")
@Route(value = "", layout = MenuTemplate.class)
public class NasView extends VerticalLayout {

    private final Filter filter = new Filter();
    private final NasService nasService;
    private final SecurityService securityService;

    @Autowired
    public NasView(NasService nasService, SecurityService securityService) {
        this.nasService = nasService;
        this.securityService = securityService;
        buildView();
    }

    private void buildView() {
        setHeightFull();
        setSpacing(false);

        RoleDto role = securityService.getLoggedUserRole();
        Grid<NasDto> grid = new Grid<>(NasDto.class, false);
        grid.addColumns("nasName", "shortName", "description");
        grid.addColumn((ValueProvider<NasDto, String>) nasDto
                -> nasDto.getSecret().replaceAll(".", "*")).setHeader("Secret");
        grid.addColumns("server", "community", "ports", "type");
        DataProvider<NasDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> nasService.pageNasRecords(filter.getSearchText(), pageable),
                value -> nasService.countNasRecords(filter.getSearchText()))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.setDataProvider(dataProvider);
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setMinHeight("500px");
        grid.setHeight("100%");

        ConfirmationDialog nasDeleteDialog = new ConfirmationDialog("400px");
        nasDeleteDialog.setTitle("Delete NAS");
        nasDeleteDialog.setConfirmButtonCaption("Delete");
        nasDeleteDialog.setConfirmListener(() -> {
            NasDto nasDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(nasDto)) {
                try {
                    nasService.deleteNas(nasDto);
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to delete NAS. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                }
            }
            nasDeleteDialog.setOpened(false);
        });

        NasEditDialog nasEditDialog = new NasEditDialog(nasService,
                (source, bean) -> grid.getDataProvider().refreshItem(bean));
        NasCreateDialog nasCreateDialog = new NasCreateDialog(nasService,
                (source, bean) -> grid.getDataProvider().refreshAll());

        Button createBtn = new Button("Create", event -> nasCreateDialog.startNasCreation());
        createBtn.setEnabled(role == RoleDto.ADMIN);
        Button editBtn = new Button("Edit", event -> {
            NasDto nasDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(nasDto)) {
                nasEditDialog.editNas(nasDto);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> {
            NasDto nasDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(nasDto)) {
                nasDeleteDialog.setDescription("Are you sure you want to delete '" + nasDto.getNasName() + "' NAS?");
                nasDeleteDialog.setOpened(true);
            }
        });
        deleteBtn.setEnabled(false);

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

        add(new H4("Data from Radius DB - \"nas\" table"));
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("NAS"));
        horizontalLayout.add(createBtn);
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

    static class NasCreateDialog extends NasFormDialog {

        private final CreationListener<NasDto> creationListener;

        NasCreateDialog(NasService nasService,
                        CreationListener<NasDto> creationListener) {
            super(nasService);
            this.creationListener = creationListener;
        }

        @Override
        String getDialogTitle() {
            return "Create NAS";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Create", event -> {
                NasDto nasDto = new NasDto();
                if (binder.writeBeanIfValid(nasDto)) {
                    try {
                        nasDto = nasService.createNas(nasDto);
                        creationListener.onCreated(this, nasDto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to create NAS. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void startNasCreation() {
            setOpened(true);
            binder.readBean(new NasDto());
        }

    }

    static class NasEditDialog extends NasFormDialog {

        private final UpdateListener<NasDto> updateListener;
        private ConfirmationDialog confirmationDialog;
        private String originNasName;

        NasEditDialog(NasService nasService,
                      UpdateListener<NasDto> updateListener) {
            super(nasService);
            this.updateListener = updateListener;

            confirmationDialog = new ConfirmationDialog();
            confirmationDialog.setTitle("Confirm NAS change");
        }

        @Override
        String getDialogTitle() {
            return "Edit NAS";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Save", event -> {
                BinderValidationStatus<NasDto> validationStatus = binder.validate();
                if (validationStatus.isOk()) {
                    try {
                        NasDto dto = binder.getBean();
                        if (!Objects.equals(dto.getNasName(), originNasName)
                                && nasService.existsNasGroupWithIpAddress(originNasName)) {
                            confirmationDialog.setDescription(String.format("NAS '%s' found in a NAS group - " +
                                    "do not forget to update NAS group configuration. " +
                                    "Are you sure you want to change this NAS?", originNasName));
                            confirmationDialog.setConfirmListener(() -> {
                                confirmationDialog.setOpened(false);
                                saveNas(dto);
                            });
                            confirmationDialog.setOpened(true);
                        } else {
                            saveNas(dto);
                        }
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to update NAS. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void editNas(NasDto dto) {
            setOpened(true);
            originNasName = dto.getNasName();
            binder.setBean(dto);
        }

        private void saveNas(NasDto dto) {
            dto = nasService.updateNas(dto);
            updateListener.onUpdated(this, dto);
        }

    }

    static abstract class NasFormDialog extends Dialog {

        final NasService nasService;
        final Binder<NasDto> binder;

        NasFormDialog(NasService nasService) {
            this.nasService = nasService;

            TextField name = new TextField("Name");
            name.setValueChangeMode(ValueChangeMode.EAGER);
            TextField shortName = new TextField("Short name");
            shortName.setValueChangeMode(ValueChangeMode.EAGER);
            TextField type = new TextField("Type");
            type.setValueChangeMode(ValueChangeMode.EAGER);
            NumberField port = new NumberField("Port");
            port.setValueChangeMode(ValueChangeMode.EAGER);
            PasswordField secret = new PasswordField("Secret");
            secret.setValueChangeMode(ValueChangeMode.EAGER);
            TextField server = new TextField("Server");
            server.setValueChangeMode(ValueChangeMode.EAGER);
            TextField community = new TextField("Community");
            community.setValueChangeMode(ValueChangeMode.EAGER);
            TextArea description = new TextArea("Description");
            description.setValueChangeMode(ValueChangeMode.EAGER);

            FormLayout formLayout = new FormLayout();
            formLayout.setWidthFull();
            formLayout.setMaxWidth("700px");
            formLayout.add(name, shortName, server, port, secret, type, community, description);
            formLayout.setResponsiveSteps(
                    new FormLayout.ResponsiveStep("0px", 1),
                    new FormLayout.ResponsiveStep("450px", 2));

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
            controlsLayout.add(getConfirmBtn());
            controlsLayout.setWidthFull();

            add(new H3(getDialogTitle()));
            add(formLayout);
            add(new Hr());
            add(controlsLayout);

            binder = new BeanValidationBinder<>(NasDto.class);
            binder.bind(name, "nasName");
            binder.bind(shortName, "shortName");
            binder.bind(type, "type");
            binder.forField(port)
                    .withConverter(new DoubleToIntegerConverter("Port must be number " +
                            "between 1 and " + 65535 + "."))
                    .bind("ports");
            binder.bind(secret, "secret");
            binder.bind(server, "server");
            binder.bind(community, "community");
            binder.bind(description, "description");
        }

        abstract String getDialogTitle();

        abstract Button getConfirmBtn();

    }

}
