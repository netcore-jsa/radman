package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.ui.component.wizard.DataStorage;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.nas.widget.NasForm;

import java.util.List;

public class NasStep implements WizardStep<DemoDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<? extends DataStorage>> steps;
    private NasService nasService;
    private final NasForm nasForm;

    public NasStep(NasService nasService, List<WizardStep<? extends DataStorage>> steps) {
        this.nasService = nasService;

        nasForm = new NasForm();
        contentLayout.withComponent(nasForm);

        this.steps = steps;
    }

    @Override
    public Component getContent() {
        steps.add(new NasStepSecond(nasService));
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return true; //TODO wizard - check binder
    }

    @Override
    public void writeDataToStorage(@NonNull DemoDataStorage dataStorage) {
        //            dataStorage.setNasDto(binder.getBean());
    }

    private static class NasStepSecond implements WizardStep<DemoDataStorage> {

        private static final String NO = "No";
        private static final String EXISTING_GROUPS = "Existing groups";
        private static final String NEW_GROUP = "New group";

        private final VVerticalLayout contentLayout = new VVerticalLayout();

        public NasStepSecond(NasService nasService) {
            VHorizontalLayout groupHolder = new VHorizontalLayout();
            RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
            radioGroup.setItems(NO, EXISTING_GROUPS, NEW_GROUP);

            ComboBox<NasGroupDto> existingGroups = new ComboBox<>("Group name");
            existingGroups.setItemLabelGenerator(NasGroupDto::getGroupName);
            existingGroups.setDataProvider(new CallbackDataProvider<>(query ->
                    nasService.pageNasGroupRecords(query.getFilter().orElse(null), PageRequest.of(query.getOffset(),
                            query.getLimit(), new Sort(Sort.Direction.ASC, "id"))).stream(),
                    query -> (int) nasService.countNasGroupRecords(query.getFilter().orElse(null))));

            VTextField newGroupName = new VTextField("New group name")
                    .withRequired(true);

            radioGroup.addValueChangeListener(event -> {
                switch (event.getValue()) {
                    case NO:
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
            contentLayout.add(radioGroup);
        }

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return true; //TODO check binder
        }

        @Override
        public void writeDataToStorage(@NonNull DemoDataStorage dataStorage) {
//            dataStorage.setNasDto(binder.getBean());
        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

    }

}
