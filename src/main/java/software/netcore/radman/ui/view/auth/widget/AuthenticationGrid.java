package software.netcore.radman.ui.view.auth.widget;

import lombok.NonNull;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationsDto;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.support.Filter;

public class AuthenticationGrid extends AuthGrid<AuthenticationsDto, AuthenticationDto> {

    private final AuthService authService;
    private final AuthenticationAttributeAssigmentDialog assigmentDialog;

    public AuthenticationGrid(@NonNull AuthService authService,
                              @NonNull AttributeService attributeService,
                              @NonNull RadiusUserService radiusUserService,
                              @NonNull SecurityService securityService,
                              @NonNull AuthFormConfiguration formConfig) {
        super(securityService);
        this.authService = authService;
        this.assigmentDialog = new AuthenticationAttributeAssigmentDialog(authService, attributeService,
                radiusUserService, formConfig, (source, bean) -> refreshGrid());
        refreshGrid();
    }

    @Override
    String getGridTitle() {
        return "Authentication";
    }

    @Override
    AuthenticationsDto getAuthsDto(Filter filter) {
        return authService.getAuthentications(filter);
    }

    @Override
    public AttributeAssignmentDialog<AuthenticationDto, ? extends AttributeDto> getAssigmentDialog() {
        return assigmentDialog;
    }

    @Override
    void deleteAssigment(String name, String type) {
        authService.deleteAuthentication(name, type);
    }

}
