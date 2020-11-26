package software.netcore.radman.ui.view.auth.widget;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.buisness.service.auth.dto.AuthDto;
import software.netcore.radman.buisness.service.auth.dto.AuthTarget;
import software.netcore.radman.buisness.service.auth.dto.RadiusOp;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupFilter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserFilter;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.converter.AttributeDtoToNameConverter;
import software.netcore.radman.ui.converter.RadiusGroupDtoToNameConverter;
import software.netcore.radman.ui.converter.RadiusUserDtoToNameConverter;
import software.netcore.radman.ui.notification.ErrorNotification;

@Slf4j
public abstract class AttributeAssignmentDialog<T extends AuthDto, U extends AttributeDto> extends Dialog {

    private final RadiusUserService userService;
    private final CreationListener<Void> creationListener;

    Binder<T> binder;
    private ComboBox<RadiusUserDto> username;
    private ComboBox<RadiusGroupDto> groupName;
    private AbstractSinglePropertyField<? extends AbstractField<?, ?>, String> value;

    public AttributeAssignmentDialog(RadiusUserService userService,
                                     CreationListener<Void> creationListener) {
        this.userService = userService;
        this.creationListener = creationListener;
    }

    private void build() {
        removeAll();
        binder = new BeanValidationBinder<>(getClazz());

        HorizontalLayout authTargetConfigLayout = new HorizontalLayout();
        username = new ComboBox<>("Username");
        username.setItemLabelGenerator(RadiusUserDto::getUsername);
        groupName = new ComboBox<>("Group name");
        groupName.setItemLabelGenerator(RadiusGroupDto::getName);
        username.setDataProvider(new CallbackDataProvider<>(query ->
                userService.pageRadiusUsers(new RadiusUserFilter(query.getFilter().orElse(null),
                        true, false), PageRequest.of(query.getOffset(),
                        query.getLimit(), new Sort(Sort.Direction.ASC, "id"))).stream(),
                query -> (int) userService.countRadiusUsers(new RadiusUserFilter(query.getFilter()
                        .orElse(null), true, false))));
        groupName.setDataProvider(new CallbackDataProvider<>(query ->
                userService.pageRadiusUsersGroup(new RadiusGroupFilter(query.getFilter().orElse(null),
                        true, false), PageRequest.of(query.getOffset(),
                        query.getLimit(), new Sort(Sort.Direction.ASC, "id"))).stream(),
                query -> (int) userService.countRadiusUsersGroup(new RadiusGroupFilter(query.getFilter()
                        .orElse(null), true, false))));

        Select<AuthTarget> authTargetSelect = new Select<>(AuthTarget.values());
        authTargetSelect.setLabel("Type");
        authTargetSelect.setItemLabelGenerator(AuthTarget::getValue);
        authTargetSelect.setTextRenderer(AuthTarget::getValue);
        authTargetSelect.addValueChangeListener(event -> {
            binder.removeBinding("name");
            authTargetConfigLayout.removeAll();
            if (event.getValue() == AuthTarget.RADIUS_USER) {
                authTargetConfigLayout.add(username);
                binder.forField(username)
                        .withConverter(new RadiusUserDtoToNameConverter())
                        .bind("name");
            } else {
                authTargetConfigLayout.add(groupName);
                binder.forField(groupName)
                        .withConverter(new RadiusGroupDtoToNameConverter())
                        .bind("name");
            }
            authTargetConfigLayout.add(authTargetSelect);
        });
        authTargetSelect.setEmptySelectionAllowed(false);
        authTargetConfigLayout.add(authTargetSelect);

        HorizontalLayout attrConfigLayout = new HorizontalLayout();
        ComboBox<U> attribute = new ComboBox<>("Attribute");
        attribute.setItemLabelGenerator(AttributeDto::getName);
        attribute.setDataProvider((ComboBox.FetchItemsCallback<U>)
                        (searchText, offset, limit) -> pageAttributes(new AttributeFilter(searchText,
                                        true, false),
                                PageRequest.of(offset, limit, new Sort(Sort.Direction.ASC, "id")))
                                .stream(),
                (SerializableFunction<String, Integer>) searchText ->
                        (int) countAttributes(new AttributeFilter(searchText, true,
                                false)));
        attribute.addValueChangeListener(event -> {
            if (event.getValue() != null && event.getValue().isSensitiveData()) {
                if (!(value instanceof PasswordField)) {
                    AbstractSinglePropertyField<? extends AbstractField<?, ?>, String> newValueField
                            = buildPasswordValueField();
                    attrConfigLayout.replace(value, newValueField);
                    value = newValueField;
                }
            } else {
                if (!(value instanceof TextField)) {
                    AbstractSinglePropertyField<? extends AbstractField<?, ?>, String> newValueField
                            = buildTextValueField();
                    attrConfigLayout.replace(value, newValueField);
                    value = newValueField;
                }
            }
        });

        Select<RadiusOp> opSelect = new Select<>(RadiusOp.values());
        opSelect.setLabel("Operation");
        opSelect.setItemLabelGenerator(RadiusOp::getValue);
        opSelect.setTextRenderer(RadiusOp::getValue);
        opSelect.setWidth("75px");
        value = buildTextValueField();

        binder.bind(authTargetSelect, "authTarget");
        binder.forField(attribute)
                .withConverter(new AttributeDtoToNameConverter<>())
                .bind("attribute");
        binder.bind(opSelect, "op");

        Button assignBtn = new Button("Assign", event -> {
            T dto = getNewBeanInstance();
            if (binder.writeBeanIfValid(dto)) {
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

        attrConfigLayout.add(attribute, opSelect, value);

        add(new H3(getDialogTitle()));
        add(authTargetConfigLayout);
        add(attrConfigLayout);
        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        controlsLayout.setWidthFull();
        controlsLayout.add(cancelBtn);
        controlsLayout.add(assignBtn);
        add(new Hr());
        add(controlsLayout);
    }

    private AbstractSinglePropertyField<? extends AbstractField<?, ?>, String> buildTextValueField() {
        binder.removeBinding("value");
        TextField valueField = new TextField("Value");
        valueField.setValueChangeMode(ValueChangeMode.EAGER);
        valueField.setClearButtonVisible(false);
        binder.bind(valueField, "value");
        return valueField;
    }

    private AbstractSinglePropertyField<? extends AbstractField<?, ?>, String> buildPasswordValueField() {
        binder.removeBinding("value");
        PasswordField valueField = new PasswordField("Value");
        valueField.setValueChangeMode(ValueChangeMode.EAGER);
        valueField.setClearButtonVisible(false);
        binder.bind(valueField, "value");
        return valueField;
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
        binder.readBean(auth);
    }

}
