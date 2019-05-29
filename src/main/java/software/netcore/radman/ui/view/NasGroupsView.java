package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;
import software.netcore.radman.ui.support.Filter;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("Radman: NAS groups")
@Route(value = "nas_groups", layout = MainTemplate.class)
public class NasGroupsView extends Div {

    private final Filter filter = new Filter();
    private final NasService nasService;

    @Autowired
    public NasGroupsView(NasService nasService) {
        this.nasService = nasService;
        buildView();
    }

    private void buildView() {
        Grid<NasGroupDto> grid = new Grid<>(NasGroupDto.class, false);
        grid.setColumns("groupName", "nasIpAddress", "nasPortId");
        DataProvider<NasGroupDto, Object> dataProvider = new SpringDataProviderBuilder<>(
                (pageable, o) -> nasService.pageNasGroupRecords(filter.getSearchText(), pageable),
                value -> nasService.countNasGroupRecords(filter.getSearchText()))
                .withDefaultSort("id", SortDirection.ASCENDING)
                .build();
        grid.getColumns().forEach(column -> column.setResizable(true));
        grid.setColumnReorderingAllowed(true);
        grid.setDataProvider(dataProvider);

        NasGroupCreateDialog createDialog = new NasGroupCreateDialog(nasService,
                (source, bean) -> grid.getDataProvider().refreshAll());
        NasGroupEditDialog editDialog = new NasGroupEditDialog(nasService,
                (source, bean) -> grid.getDataProvider().refreshItem(bean));
        ConfirmationDialog deleteDialog = new ConfirmationDialog("400px");
        deleteDialog.setTitle("Delete NAS group");
        deleteDialog.setDescription("Are you sure?");
        deleteDialog.setConfirmButtonCaption("Delete");
        deleteDialog.setConfirmListener(() -> {
            NasGroupDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                nasService.deleteNasGroup(dto);
                deleteDialog.setOpened(false);
            }
        });

        Button createBtn = new Button("Create", event -> createDialog.startNasGroupCreation());
        Button editBtn = new Button("Edit", event -> {
            NasGroupDto dto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
            if (Objects.nonNull(dto)) {
                editDialog.editNasGroup(dto);
            }
        });
        editBtn.setEnabled(false);
        Button deleteBtn = new Button("Delete", event -> deleteDialog.setOpened(true));
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

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        horizontalLayout.add(new H3("NAS groups"));
        horizontalLayout.add(createBtn);
        horizontalLayout.add(editBtn);
        horizontalLayout.add(deleteBtn);
        horizontalLayout.add(search);
        add(horizontalLayout);
        add(grid);
    }

    static abstract class NasGroupFormDialog extends Dialog {

        final NasService nasService;
        final Binder<NasGroupDto> binder;

        NasGroupFormDialog(NasService nasService) {
            this.nasService = nasService;

            TextField groupname = new TextField("Group name");
            groupname.setValueChangeMode(ValueChangeMode.EAGER);
            TextField nasIpAddress = new TextField("IP address");
            nasIpAddress.setValueChangeMode(ValueChangeMode.EAGER);
            TextField nasPortId = new TextField("Port ID");
            nasPortId.setValueChangeMode(ValueChangeMode.EAGER);

            binder = new BeanValidationBinder<>(NasGroupDto.class);
            binder.bind(groupname, "groupName");
            binder.bind(nasIpAddress, "nasIpAddress");
            binder.bind(nasPortId, "nasPortId");

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
            controlsLayout.add(getConfirmBtn());
            controlsLayout.setWidthFull();

            add(new H3(getDialogTitle()));
            add(new FormLayout(groupname, nasIpAddress, nasPortId));
            add(new Hr());
            add(controlsLayout);
        }

        abstract String getDialogTitle();

        abstract Button getConfirmBtn();

    }

    static class NasGroupCreateDialog extends NasGroupFormDialog {

        private final CreationListener<NasGroupDto> creationListener;

        NasGroupCreateDialog(NasService nasService,
                             CreationListener<NasGroupDto> creationListener) {
            super(nasService);
            this.creationListener = creationListener;
        }

        @Override
        String getDialogTitle() {
            return "Create NAS group";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Create", event -> {
                NasGroupDto dto = new NasGroupDto();
                if (binder.writeBeanIfValid(dto)) {
                    try {
                        dto = nasService.createNasGroup(dto);
                        creationListener.onCreated(this, dto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to create NAS group. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void startNasGroupCreation() {
            setOpened(true);
            binder.readBean(new NasGroupDto());
        }

    }

    static class NasGroupEditDialog extends NasGroupFormDialog {

        private final UpdateListener<NasGroupDto> updateListener;

        NasGroupEditDialog(NasService nasService, UpdateListener<NasGroupDto> updateListener) {
            super(nasService);
            this.updateListener = updateListener;
        }

        @Override
        String getDialogTitle() {
            return "Edit NAS group";
        }

        @Override
        Button getConfirmBtn() {
            return new Button("Save", event -> {
                BinderValidationStatus<NasGroupDto> validationStatus = binder.validate();
                if (validationStatus.isOk()) {
                    try {
                        NasGroupDto dto = binder.getBean();
                        dto = nasService.updateNasGroup(dto);
                        updateListener.onUpdated(this, dto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to update NAS group. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });
        }

        void editNasGroup(NasGroupDto dto) {
            setOpened(true);
            binder.setBean(dto);
        }

    }

}
