package software.netcore.radman.ui.view.nas.widget;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;
import software.netcore.radman.buisness.service.nas.NasService;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.ui.CreationListener;
import software.netcore.radman.ui.notification.ErrorNotification;

@Slf4j
public class NasCreateDialog extends Dialog {

    private static final String TITLE = "Create NAS";

    private final NasService nasService;
    private final CreationListener<NasDto> creationListener;

    private final NasForm nasForm;

    public NasCreateDialog(NasService nasService,
                           CreationListener<NasDto> creationListener) {
        this.nasService = nasService;
        this.creationListener = creationListener;

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
    }

    Button getConfirmBtn() {
        return new Button("Create", event -> {
            NasDto nasDto;
            if (nasForm.isValid()) {
                try {
                    nasDto = nasService.createNas(nasForm.getBean());
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

    public void startNasCreation() {
        setOpened(true);
        nasForm.setBean(new NasDto());
    }

}
