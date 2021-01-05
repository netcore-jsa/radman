package software.netcore.radman.ui.view.auth.widget;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
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
import software.netcore.radman.ui.converter.AttributeDtoToNameConverter;
import software.netcore.radman.ui.converter.RadiusGroupDtoToNameConverter;
import software.netcore.radman.ui.converter.RadiusUserDtoToNameConverter;

import java.util.ArrayList;
import java.util.List;

public class AuthForm<T extends AuthDto, U extends AttributeDto> extends FormLayout {

    @FunctionalInterface
    public interface AttributePager<U extends AttributeDto> {

        Page<U> pageAttributes(String searchText, int offset, int limit);

    }

    @FunctionalInterface
    public interface AttributeCounter {

        int countAttributes(AttributeFilter filter);

    }

    private final Binder<T> binder;
    private final AuthFormConfiguration formConfig;
    @Getter
    private final ComboBox<RadiusUserDto> username;
    @Getter
    private final ComboBox<RadiusGroupDto> groupName;
    @Getter
    private final Select<AuthTarget> authTargetSelect;

    private AbstractSinglePropertyField<? extends AbstractField<?, ?>, String> value;

    public AuthForm(@NonNull Class<T> clazz,
                    @NonNull RadiusUserService userService,
                    AuthFormConfiguration formConfig,
                    U attributeItem,
                    AttributePager<U> attributePager,
                    AttributeCounter attributeCounter) {

        this.formConfig = formConfig;
        binder = new BeanValidationBinder<>(clazz);
        try {
            binder.setBean(clazz.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        authTargetSelect = new Select<>(AuthTarget.values());
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
        if (formConfig.isShowFullForm() || formConfig.isPredefinedUser()) {
            attribute.setDataProvider((ComboBox.FetchItemsCallback<U>)
                            (searchText, offset, limit) -> attributePager.pageAttributes(searchText, offset, limit)
                                    .stream(),
                    (SerializableFunction<String, Integer>) searchText ->
                            (int) attributeCounter.countAttributes(new AttributeFilter(searchText, true,
                                    false)));
        } else {
            attribute.setItems(attributeItem);
            List<AttributeDto> list = new ArrayList<>();
            list.add(attributeItem);
            attribute.setDataProvider((ListDataProvider<U>) new ListDataProvider<>(list));
            attribute.setValue(attributeItem);
        }

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

        if (formConfig.isShowFullForm()) {
            authTargetSelect.setValue(AuthTarget.RADIUS_USER);
            attrConfigLayout.add(attribute, opSelect, value);
            add(new VVerticalLayout()
                    .withComponents(authTargetConfigLayout)
                    .withComponents(attrConfigLayout));
        } else if (formConfig.isPredefinedUser()) {
            authTargetSelect.setValue(AuthTarget.RADIUS_USER);
            username.setValue(formConfig.getPredefinedUserDto());
            attrConfigLayout.add(attribute, opSelect, value);
            add(new VVerticalLayout()
                    .withComponents(attrConfigLayout));
        } else {
            if (formConfig.isPredefinedAttrTargetAsUser()) {
                authTargetSelect.setValue(AuthTarget.RADIUS_USER);
                attribute.setValue(attributeItem);
            } else if (formConfig.isPredefinedAttrTargetAsGroup()) {
                authTargetSelect.setValue(AuthTarget.RADIUS_GROUP);
                attribute.setValue(attributeItem);
            }

            authTargetConfigLayout.remove(authTargetSelect);
            add(new VHorizontalLayout()
                    .withComponents(authTargetConfigLayout)
                    .withComponents(opSelect)
                    .withComponents(value));
        }
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

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public T getBean() {
        return binder.getBean();
    }

    public void setBean(T attributeDto) {
        binder.setBean(attributeDto);
    }

}
