package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.nas.widget.NasForm;

import java.util.List;
import java.util.Objects;

/**
 * @author daniel
 * @since v. 1.0.3
 */
public class NasStep implements WizardStep<NewEntityWizardDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;
    private final NasService nasService;
    private final NasForm nasForm;

    public NasStep(NasService nasService, List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.nasService = nasService;

        nasForm = new NasForm();
        nasForm.setBean(new NasDto());
        contentLayout.withComponent(nasForm);

        this.steps = steps;
    }

    @Override
    public Component getContent() {
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return nasForm.isValid();
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        dataStorage.setNasDto(nasForm.getBean());
    }

    @Override
    public void onTransition() {
        steps.add(new NasStepSecond(nasService, nasForm.getBean().getNasName()));
    }

    private static class NasStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private static final String NO = "No";
        private static final String EXISTING_GROUPS = "Existing groups";
        private static final String NEW_GROUP = "New group";

        private final VVerticalLayout contentLayout = new VVerticalLayout();
        private final Binder<NasGroupDto> binder = new Binder<>();

        private final RadioButtonGroup<String> radioGroup;
        private final ComboBox<NasGroupDto> existingGroups;
        private final TextField newGroupName;
        private final String nasIpAddress;

        public NasStepSecond(NasService nasService, String nasIpAddress) {
            this.nasIpAddress = nasIpAddress;
            VHorizontalLayout groupHolder = new VHorizontalLayout();
            radioGroup = new RadioButtonGroup<>();
            radioGroup.setItems(NO, EXISTING_GROUPS, NEW_GROUP);
            radioGroup.setItemEnabledProvider(item -> {
                if (EXISTING_GROUPS.equals((String) item) && nasService.countNasGroupRecords("") == 0) {
                    return false;
                }

                return true;
            });

            existingGroups = new ComboBox<>("Group name");
            existingGroups.setItemLabelGenerator(NasGroupDto::getGroupName);
            existingGroups.setDataProvider(new CallbackDataProvider<>(query ->
                    nasService.pageNasGroupRecords(query.getFilter().orElse(null), PageRequest.of(query.getOffset(),
                            query.getLimit(), new Sort(Sort.Direction.ASC, "id"))).stream(),
                    query -> (int) nasService.countNasGroupRecords(query.getFilter().orElse(null))));

            newGroupName = new TextField("New group name");
            newGroupName.setRequired(true);
            binder.forField(newGroupName)
                    .asRequired("Group name is required")
                    .bind(NasGroupDto::getGroupName, NasGroupDto::setGroupName);

            radioGroup.addValueChangeListener(event -> {
                switch (event.getValue()) {
                    case NO:
                        groupHolder.removeAll();
                        break;
                    case EXISTING_GROUPS:
                        groupHolder.removeAll();
                        groupHolder.withComponent(existingGroups);
                        break;
                    case NEW_GROUP:
                        groupHolder.removeAll();
                        groupHolder.withComponent(newGroupName);
                        break;
                }
            });
            contentLayout.add(radioGroup, groupHolder);
        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return binder.validate().isOk();
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
            if (!radioGroup.getValue().equals(NO)) {
                NasGroupDto nasGroupDto = new NasGroupDto();
                if (radioGroup.getValue().equals(EXISTING_GROUPS) && Objects.nonNull(existingGroups.getValue())) {
                    nasGroupDto.setGroupName(existingGroups.getValue().getGroupName());
                } else if (radioGroup.getValue().equals(NEW_GROUP)) {
                    nasGroupDto.setGroupName(newGroupName.getValue());
                }
                nasGroupDto.setNasIpAddress(nasIpAddress);

                dataStorage.getNasGroupDtos().add(nasGroupDto);
            }
        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

    }

}
