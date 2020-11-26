package software.netcore.radman.ui.component.wizard.demo;

import com.vaadin.flow.component.Component;
import lombok.NonNull;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.ui.component.wizard.WizardStep;
import software.netcore.radman.ui.view.userGroups.widget.UserGroupsForm;

import java.util.List;

public class UserGroupStep implements WizardStep<NewEntityWizardDataStorage> {

    private final VVerticalLayout contentLayout = new VVerticalLayout();
    private final List<WizardStep<NewEntityWizardDataStorage>> steps;
    private final UserGroupsForm userGroupsForm;

    public UserGroupStep(List<WizardStep<NewEntityWizardDataStorage>> steps) {
        this.steps = steps;
        userGroupsForm = new UserGroupsForm();
        userGroupsForm.setBean(new RadiusGroupDto());
        contentLayout.withComponent(userGroupsForm);
    }

    @Override
    public Component getContent() {
        steps.add(new UserGroupStepSecond());
        return contentLayout;
    }

    @Override
    public boolean isValid() {
        return userGroupsForm.isValid();
    }

    @Override
    public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {
        dataStorage.setRadiusGroupDto(userGroupsForm.getBean());
    }

    private class UserGroupStepSecond implements WizardStep<NewEntityWizardDataStorage> {

        private final VVerticalLayout contentLayout = new VVerticalLayout();

        @Override
        public Component getContent() {
            return contentLayout;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void writeDataToStorage(@NonNull NewEntityWizardDataStorage dataStorage) {

        }

        @Override
        public boolean hasNextStep() {
            return false;
        }

    }

}
