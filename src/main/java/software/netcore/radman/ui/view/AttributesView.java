package software.netcore.radman.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.SpringDataProviderBuilder;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.ui.CancelListener;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.menu.MainTemplate;

/**
 * @since v. 1.0.0
 */
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

    @SuppressWarnings("Duplicates")
    abstract static class AttributeGrid<T extends AttributeDto> extends Div {

        AttributeService attributeService;

        AttributeGrid(AttributeService attributeService,
                      AttributeCreationDialog<T> creationDialog) {
            this.attributeService = attributeService;
            setWidth("50%");

            Grid<T> authorizationDtoGrid = new Grid<>(getClazz(), false);
            authorizationDtoGrid.setColumns("name", "description", "sensitive");
            DataProvider<T, Object> dataProvider = new SpringDataProviderBuilder<>(
                    (pageable, o) -> pageAttributes(pageable), value -> countAttributes())
                    .withDefaultSort("id", SortDirection.ASCENDING)
                    .build();
            authorizationDtoGrid.getColumns().forEach(column -> column.setResizable(true));
            authorizationDtoGrid.setColumnReorderingAllowed(true);
            authorizationDtoGrid.setDataProvider(dataProvider);
            authorizationDtoGrid.setWidth("700px");

            Button createBtn = new Button("Create", event -> creationDialog.setOpened(true));
            Button editBtn = new Button("Edit");
            editBtn.setEnabled(false);
            Button deleteBtn = new Button("Delete");
            deleteBtn.setEnabled(false);

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
            horizontalLayout.add(new H3(getGridTitle()));
            horizontalLayout.add(createBtn);
            horizontalLayout.add(editBtn);
            horizontalLayout.add(deleteBtn);

            add(horizontalLayout);
            add(authorizationDtoGrid);
        }

        abstract String getGridTitle();

        abstract Class<T> getClazz();

        abstract Page<T> pageAttributes(Pageable pageable);

        abstract int countAttributes();

    }

    static class AuthenticationAttributeGrid extends AttributeGrid<AuthenticationAttributeDto> {

        AuthenticationAttributeGrid(AttributeService attributeService) {
            super(attributeService,
                    new AuthenticationAttributeCreationDialog(attributeService,
                            (source, bean) -> {
                            }));
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
        Page<AuthenticationAttributeDto> pageAttributes(Pageable pageable) {
            return attributeService.pageAuthenticationAttributeRecords(pageable);
        }

        @Override
        int countAttributes() {
            return attributeService.countAuthenticationAttributeRecords();
        }

    }

    static class AuthorizationAttributeGrid extends AttributeGrid<AuthorizationAttributeDto> {

        AuthorizationAttributeGrid(AttributeService attributeService) {
            super(attributeService,
                    new AuthorizationAttributeCreationDialog(attributeService,
                            (source, bean) -> {

                            }));
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
        Page<AuthorizationAttributeDto> pageAttributes(Pageable pageable) {
            return attributeService.pageAuthorizationAttributeRecords(pageable);
        }

        @Override
        int countAttributes() {
            return attributeService.countAuthorizationAttributeRecords();
        }

    }

    abstract static class AttributeCreationDialog<T extends AttributeDto> extends Dialog {

        AttributeService attributeService;
        private CreationListener<T> creationListener;
        private CancelListener cancelListener;

        AttributeCreationDialog(AttributeService attributeService,
                                CreationListener<T> creationListener) {
            FormLayout formLayout = new FormLayout();
            formLayout.add(new H3(getDialogTitle()));

            TextField username = new TextField("Name");
            username.setRequiredIndicatorVisible(true);
            username.setValueChangeMode(ValueChangeMode.EAGER);
            username.setWidthFull();

            TextArea description = new TextArea("Description");
            description.setValueChangeMode(ValueChangeMode.EAGER);
            description.setWidthFull();

            Checkbox sensitive = new Checkbox("Sensitive");
            sensitive.setWidthFull();

            Button createBtn = new Button("Create");
            Button cancelBtn = new Button("Cancel", event -> setOpened(false));

            HorizontalLayout controlsLayout = new HorizontalLayout();
            controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            controlsLayout.setWidthFull();
            controlsLayout.add(cancelBtn);
            controlsLayout.add(createBtn);

            formLayout.add(username);
            formLayout.add(description);
            formLayout.add(sensitive);
            formLayout.add(controlsLayout);
            formLayout.setMaxWidth("500px");
            add(formLayout);
        }

        abstract String getDialogTitle();

        abstract T create(T attributeDto);

    }

    static class AuthenticationAttributeCreationDialog extends AttributeCreationDialog<AuthenticationAttributeDto> {

        AuthenticationAttributeCreationDialog(AttributeService attributeService,
                                              CreationListener<AuthenticationAttributeDto> creationListener) {
            super(attributeService, creationListener);
        }

        @Override
        String getDialogTitle() {
            return "New authentication attribute";
        }

        @Override
        AuthenticationAttributeDto create(AuthenticationAttributeDto attributeDto) {
            return attributeService.createAuthenticationAttribute(attributeDto);
        }

    }

    static class AuthorizationAttributeCreationDialog extends AttributeCreationDialog<AuthorizationAttributeDto> {

        AuthorizationAttributeCreationDialog(AttributeService attributeService,
                                             CreationListener<AuthorizationAttributeDto> creationListener) {
            super(attributeService, creationListener);
        }

        @Override
        String getDialogTitle() {
            return "New authorization attribute";
        }

        @Override
        AuthorizationAttributeDto create(AuthorizationAttributeDto attributeDto) {
            return attributeService.createAuthorizationAttribute(attributeDto);
        }

    }

    static class AttributeEditForm extends FormLayout {

    }

}
