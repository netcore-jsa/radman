package software.netcore.radman.ui.view.auth.widget;

import lombok.NonNull;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AttributeDto;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationsDto;
import software.netcore.radman.buisness.service.security.SecurityService;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.support.Filter;

public class AuthorizationGrid extends AuthGrid<AuthorizationsDto, AuthorizationDto> {

    private final AuthService authService;
    private final AuthorizationAttributeAssigmentDialog assigmentDialog;

    public AuthorizationGrid(@NonNull AuthService authService,
                             @NonNull AttributeService attributeService,
                             @NonNull RadiusUserService userService,
                             @NonNull SecurityService securityService,
                             @NonNull AuthFormConfiguration formConfig) {
        super(securityService);
        this.authService = authService;
        assigmentDialog = new AuthorizationAttributeAssigmentDialog(authService, attributeService, userService, formConfig,
                (source, bean) -> refreshGrid());
        refreshGrid();
    }

    @Override
    String getGridTitle() {
        return "Authorization";
    }

    @Override
    AuthorizationsDto getAuthsDto(Filter filter) {
        return authService.getAuthorizations(filter);
    }

    @Override
    public AttributeAssignmentDialog<AuthorizationDto, ? extends AttributeDto> getAssigmentDialog() {
        return assigmentDialog;
    }

    @Override
    void deleteAssigment(String name, String type) {
        authService.deleteAuthorization(name, type);
    }

}
