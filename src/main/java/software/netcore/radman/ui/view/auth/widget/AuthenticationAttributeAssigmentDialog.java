package software.netcore.radman.ui.view.auth.widget;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.CreationListener;

public class AuthenticationAttributeAssigmentDialog
        extends AttributeAssignmentDialog<AuthenticationDto, AuthenticationAttributeDto> {

    private final AuthService authService;
    private final AttributeService attributeService;

    public AuthenticationAttributeAssigmentDialog(AuthService authService,
                                                  AttributeService attributeService,
                                                  RadiusUserService userService,
                                                  CreationListener<Void> creationListener) {
        super(userService, creationListener);
        this.authService = authService;
        this.attributeService = attributeService;
    }

    @Override
    String getDialogTitle() {
        return "Assign authentication attribute";
    }

    @Override
    Class<AuthenticationDto> getClazz() {
        return AuthenticationDto.class;
    }

    @Override
    AuthenticationDto getNewBeanInstance() {
        return new AuthenticationDto();
    }

    @Override
    long countAttributes(AttributeFilter filter) {
        return attributeService.countAuthenticationAttributeRecords(filter);
    }

    @Override
    Page<AuthenticationAttributeDto> pageAttributes(AttributeFilter filter, Pageable pageable) {
        return attributeService.pageAuthenticationAttributeRecords(filter, pageable);
    }

    @Override
    void assignAuth(AuthenticationDto authDto) {
        authService.createAuthentication(authDto);
    }

}
