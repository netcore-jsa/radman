package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.menu.MainTemplate;
import software.netcore.radman.ui.notification.ErrorNotification;

import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@Slf4j
@PageTitle("Radman: Attributes")
@Route(value = "attributes", layout = MainTemplate.class)
public class AttributesView extends Div {

    private final AttributeService attributeService;

    @Autowired
    public AttributesView(AttributeService attributeService) {
        this.attributeService = attributeService;
        buildView();
    }

    private void buildView() {
        getElement().getStyle().set("display", "flex");
        add(new AuthenticationAttributeGrid(attributeService));
        add(new AuthorizationAttributeGrid(attributeService));
    }

    private abstract static class AttributeGrid<T extends AttributeDto> extends Div {

        private final AttributeFilter filter = new AttributeFilter(true, true);
        private final ConfirmationDialog deleteDialog;
        final AttributeService service;
        final Grid<T> grid;

        AttributeGrid(AttributeService service) {
            this.service = service;
            setWidth("50%");

            grid = new Grid<>(getClazz(), false);
            grid.setColumns("name", "description", "sensitiveData");
            DataProvider<T, Object> dataProvider = new SpringDataProviderBuilder<>(
                    (pageable, o) -> pageAttributes(filter, pageable),
                    value -> countAttributes(filter))
                    .withDefaultSort("id", SortDirection.ASCENDING)
                    .build();
            grid.getColumns().forEach(column -> column.setResizable(true));
            grid.setColumnReorderingAllowed(true);
            grid.setDataProvider(dataProvider);
            grid.setWidth("700px");

            deleteDialog = new ConfirmationDialog("365px");
            deleteDialog.setTitle("Delete attribute");
            deleteDialog.setConfirmListener(() -> {
                try {
                    T attributeDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                    deleteAttribute(attributeDto);
                    grid.getDataProvider().refreshAll();
                } catch (Exception e) {
                    log.warn("Failed to create attribute. Reason = '{}'", e.getMessage());
                    ErrorNotification.show("Error",
                            "Ooops, something went wrong, try again please");
                } finally {
                    deleteDialog.setOpened(false);
                }
            });

            Button createBtn = new Button("Create", event -> getCreationDialog().startCreation());
            Button editBtn = new Button("Edit", event -> {
                T bean = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(bean)) {
                    getEditDialog().edit(bean);
                }
            });
            editBtn.setEnabled(false);
            Button deleteBtn = new Button("Delete", event -> {
                T attributeDto = grid.getSelectionModel().getFirstSelectedItem().orElse(null);
                if (Objects.nonNull(attributeDto)) {
                    deleteDialog.setDescription("Are you sure you want to delete '"
                            + attributeDto.getName() + "' attribute?");
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

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            horizontalLayout.add(new H3(getGridTitle()));
            horizontalLayout.add(createBtn);
            horizontalLayout.add(editBtn);
            horizontalLayout.add(deleteBtn);
            horizontalLayout.add(search);
            add(horizontalLayout);
            add(grid);
        }

        abstract String getGridTitle();

        abstract Class<T> getClazz();

        abstract Page<T> pageAttributes(AttributeFilter filter, Pageable pageable);

        abstract long countAttributes(AttributeFilter filter);

        abstract void deleteAttribute(T attributeDto);

        abstract AttributeCreationDialog<T> getCreationDialog();

        abstract AttributeEditDialog<T> getEditDialog();

    }

    private static class AuthenticationAttributeGrid extends AttributeGrid<AuthenticationAttributeDto> {

        private final AttributeCreationDialog<AuthenticationAttributeDto> creationDialog;
        private final AttributeEditDialog<AuthenticationAttributeDto> editDialog;

        AuthenticationAttributeGrid(AttributeService attributeService) {
            super(attributeService);
            creationDialog = new AuthenticationAttributeCreationDialog(attributeService,
                    (source, bean) -> grid.getDataProvider().refreshAll());
            editDialog = new AuthenticationAttributeEditDialog(attributeService,
                    (source, bean) -> grid.getDataProvider().refreshItem(bean));
        }

        @Override
        String getGridTitle() {
            return "Authentication attributes";
        }

        @Override
        Class<AuthenticationAttributeDto> getClazz() {
            return AuthenticationAttributeDto.class;
        }

        @Override
        Page<AuthenticationAttributeDto> pageAttributes(AttributeFilter filter, Pageable pageable) {
            return service.pageAuthenticationAttributeRecords(filter, pageable);
        }

        @Override
        long countAttributes(AttributeFilter filter) {
            return service.countAuthenticationAttributeRecords(filter);
        }

        @Override
        void deleteAttribute(AuthenticationAttributeDto attributeDto) {
            service.deleteAuthenticationAttribute(attributeDto);
        }

        @Override
        AttributeCreationDialog<AuthenticationAttributeDto> getCreationDialog() {
            return creationDialog;
        }

        @Override
        AttributeEditDialog<AuthenticationAttributeDto> getEditDialog() {
            return editDialog;
        }

    }

    private static class AuthorizationAttributeGrid extends AttributeGrid<AuthorizationAttributeDto> {

        private final AttributeCreationDialog<AuthorizationAttributeDto> creationDialog;
        private final AttributeEditDialog<AuthorizationAttributeDto> editDialog;

        AuthorizationAttributeGrid(AttributeService attributeService) {
            super(attributeService);
            creationDialog = new AuthorizationAttributeCreationDialog(attributeService,
                    (source, bean) -> grid.getDataProvider().refreshAll());
            editDialog = new AuthorizationAttributeEditDialog(attributeService,
                    (source, bean) -> grid.getDataProvider().refreshItem(bean));
        }

        @Override
        String getGridTitle() {
            return "Authorization attributes";
        }

        @Override
        Class<AuthorizationAttributeDto> getClazz() {
            return AuthorizationAttributeDto.class;
        }

        @Override
        Page<AuthorizationAttributeDto> pageAttributes(AttributeFilter filter, Pageable pageable) {
            return service.pageAuthorizationAttributeRecords(filter, pageable);
        }

        @Override
        long countAttributes(AttributeFilter filter) {
            return service.countAuthorizationAttributeRecords(filter);
        }

        @Override
        void deleteAttribute(AuthorizationAttributeDto attributeDto) {
            service.deleteAuthorizationAttribute(attributeDto);
        }

        @Override
        AttributeCreationDialog<AuthorizationAttributeDto> getCreationDialog() {
            return creationDialog;
        }

        @Override
        AttributeEditDialog<AuthorizationAttributeDto> getEditDialog() {
            return editDialog;
        }

    }

    private abstract static class AttributeCreationDialog<T extends AttributeDto> extends Dialog {

        final AttributeService attributeService;
        private final Binder<T> binder;

        AttributeCreationDialog(AttributeService attributeService,
                                CreationListener<T> creationListener) {
            this.attributeService = attributeService;

            FormLayout formLayout = new FormLayout();
            formLayout.add(new H3(getDialogTitle()));
            TextField name = new TextField("Name");
            name.setValueChangeMode(ValueChangeMode.EAGER);
            name.setWidthFull();
            TextArea description = new TextArea("Description");
            description.setValueChangeMode(ValueChangeMode.EAGER);
            description.setWidthFull();
            Checkbox sensitive = new Checkbox("Sensitive");
            sensitive.setWidthFull();

            binder = new BeanValidationBinder<>(getClazz());
            binder.bind(name, "name");
            binder.bind(description, "description");
            binder.bind(sensitive, "sensitiveData");

            Button createBtn = new Button("Create", event -> {
                T dto = getNewBeanInstance();
                if (binder.writeBeanIfValid(dto)) {
                    try {
                        dto = create(dto);
                        creationListener.onCreated(this, dto);
                        setOpened(false);
                    } catch (DataIntegrityViolationException e) {
                        name.setInvalid(true);
                        name.setErrorMessage("Attribute with the same name already exist.");
                    } catch (Exception e) {
                        log.warn("Failed to create attribute. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.setWidthFull();
            controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
            controlsLayout.add(createBtn);

            formLayout.add(name);
            formLayout.add(description);
            formLayout.add(sensitive);
            formLayout.add(new Hr());
            formLayout.add(controlsLayout);
            formLayout.setMaxWidth("500px");
            add(formLayout);
        }

        abstract Class<T> getClazz();

        abstract String getDialogTitle();

        abstract T create(T attributeDto);

        abstract T getNewBeanInstance();

        void startCreation() {
            setOpened(true);
            binder.readBean(getNewBeanInstance());
        }

    }

    private static class AuthenticationAttributeCreationDialog extends AttributeCreationDialog<AuthenticationAttributeDto> {

        AuthenticationAttributeCreationDialog(AttributeService attributeService,
                                              CreationListener<AuthenticationAttributeDto> creationListener) {
            super(attributeService, creationListener);
        }

        @Override
        Class<AuthenticationAttributeDto> getClazz() {
            return AuthenticationAttributeDto.class;
        }

        @Override
        String getDialogTitle() {
            return "New authentication attribute";
        }

        @Override
        AuthenticationAttributeDto create(AuthenticationAttributeDto attributeDto) {
            return attributeService.createAuthenticationAttribute(attributeDto);
        }

        @Override
        AuthenticationAttributeDto getNewBeanInstance() {
            return new AuthenticationAttributeDto();
        }

    }

    private static class AuthorizationAttributeCreationDialog extends AttributeCreationDialog<AuthorizationAttributeDto> {

        AuthorizationAttributeCreationDialog(AttributeService attributeService,
                                             CreationListener<AuthorizationAttributeDto> creationListener) {
            super(attributeService, creationListener);
        }

        @Override
        Class<AuthorizationAttributeDto> getClazz() {
            return AuthorizationAttributeDto.class;
        }

        @Override
        String getDialogTitle() {
            return "New authorization attribute";
        }

        @Override
        AuthorizationAttributeDto create(AuthorizationAttributeDto attributeDto) {
            return attributeService.createAuthorizationAttribute(attributeDto);
        }

        @Override
        AuthorizationAttributeDto getNewBeanInstance() {
            return new AuthorizationAttributeDto();
        }

    }

    @SuppressWarnings("Duplicates")
    private abstract static class AttributeEditDialog<T extends AttributeDto> extends Dialog {

        final AttributeService attributeService;
        final Binder<T> binder;

        AttributeEditDialog(AttributeService attributeService, UpdateListener<T> updateListener) {
            this.attributeService = attributeService;

            FormLayout formLayout = new FormLayout();
            formLayout.add(new H3(getDialogTitle()));
            TextArea description = new TextArea("Description");
            description.setValueChangeMode(ValueChangeMode.EAGER);
            description.setWidthFull();
            Checkbox sensitive = new Checkbox("Sensitive");
            sensitive.setWidthFull();

            binder = new Binder<>(getClazz());
            binder.forField(description).bind(AttributeDto::getDescription, AttributeDto::setDescription);
            binder.forField(sensitive).bind(AttributeDto::isSensitiveData, AttributeDto::setSensitiveData);

            Button cancelBtn = new Button("Cancel", event -> setOpened(false));
            Button saveBtn = new Button("Save", event -> {
                BinderValidationStatus<T> validationStatus = binder.validate();
                if (validationStatus.isOk()) {
                    try {
                        T attributeDto = binder.getBean();
                        attributeDto = save(attributeDto);
                        updateListener.onUpdated(this, attributeDto);
                        setOpened(false);
                    } catch (Exception e) {
                        log.warn("Failed to update attribute. Reason = '{}'", e.getMessage());
                        ErrorNotification.show("Error",
                                "Ooops, something went wrong, try again please");
                    }
                }
            });

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.setWidthFull();
            controlsLayout.add(cancelBtn);
            controlsLayout.add(saveBtn);

            formLayout.add(description);
            formLayout.add(sensitive);
            formLayout.add(new Hr());
            formLayout.add(controlsLayout);
            formLayout.setMaxWidth("500px");
            add(formLayout);
        }

        abstract Class<T> getClazz();

        abstract String getDialogTitle();

        abstract T save(T attributeDto);

        void edit(T attributeDto) {
            setOpened(true);
            binder.setBean(attributeDto);
        }

    }

    private static class AuthorizationAttributeEditDialog extends AttributeEditDialog<AuthorizationAttributeDto> {

        AuthorizationAttributeEditDialog(AttributeService attributeService,
                                         UpdateListener<AuthorizationAttributeDto> updateListener) {
            super(attributeService, updateListener);
        }

        @Override
        Class<AuthorizationAttributeDto> getClazz() {
            return AuthorizationAttributeDto.class;
        }

        @Override
        String getDialogTitle() {
            return "Edit authorization attribute";
        }

        @Override
        AuthorizationAttributeDto save(AuthorizationAttributeDto attributeDto) {
            return attributeService.updateAuthorizationAttribute(attributeDto);
        }

        @Override
        void edit(AuthorizationAttributeDto attributeDto) {
            binder.setBean(attributeDto);
        }
    }

    private static class AuthenticationAttributeEditDialog extends AttributeEditDialog<AuthenticationAttributeDto> {

        AuthenticationAttributeEditDialog(AttributeService attributeService,
                                          UpdateListener<AuthenticationAttributeDto> updateListener) {
            super(attributeService, updateListener);
        }

        @Override
        Class<AuthenticationAttributeDto> getClazz() {
            return AuthenticationAttributeDto.class;
        }

        @Override
        String getDialogTitle() {
            return "Edit authentication attribute";
        }

        @Override
        AuthenticationAttributeDto save(AuthenticationAttributeDto attributeDto) {
            return attributeService.updateAuthenticationAttribute(attributeDto);
        }

    }

}
