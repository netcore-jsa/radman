package software.netcore.radman.ui.view.nas.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import lombok.extern.slf4j.Slf4j;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.ui.UpdateListener;
import software.netcore.radman.ui.component.ConfirmationDialog;
import software.netcore.radman.ui.notification.ErrorNotification;

import java.util.Objects;

@Slf4j
public class NasEditDialog extends Dialog {

    private static final String TITLE = "Edit NAS";

    private final NasService nasService;
    private final UpdateListener<NasDto> updateListener;
    private final ConfirmationDialog confirmationDialog;
    private final NasForm nasForm;

    private String originNasName;

    public NasEditDialog(NasService nasService,
                         UpdateListener<NasDto> updateListener) {
        this.nasService = nasService;
        this.updateListener = updateListener;

        nasForm = new NasForm();

        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        controlsLayout.add(new Button("Cancel", event -> setOpened(false)));
        controlsLayout.add(getConfirmBtn());
        controlsLayout.setWidthFull();

        add(new H3(TITLE));
        add(nasForm);
        add(new Hr());
        add(controlsLayout);

        confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setTitle("Confirm NAS change");
    }

    Button getConfirmBtn() {
        return new Button("Save", event -> {
            if (nasForm.isValid()) {
                try {
                    NasDto dto = nasForm.getBean();
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

    public void editNas(NasDto dto) {
        setOpened(true);
        originNasName = dto.getNasName();
        nasForm.setBean(dto);
    }

    private void saveNas(NasDto dto) {
        dto = nasService.updateNas(dto);
        updateListener.onUpdated(this, dto);
    }

}
