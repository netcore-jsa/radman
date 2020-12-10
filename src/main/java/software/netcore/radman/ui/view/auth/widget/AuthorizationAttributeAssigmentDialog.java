package software.netcore.radman.ui.view.auth.widget;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.netcore.radman.buisness.service.attribute.AttributeService;
import software.netcore.radman.buisness.service.attribute.dto.AttributeFilter;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.buisness.service.user.radius.RadiusUserService;
import software.netcore.radman.ui.CreationListener;

public class AuthorizationAttributeAssigmentDialog
        extends AttributeAssignmentDialog<AuthorizationDto, AuthorizationAttributeDto> {

    private final AuthService authService;
    private final AttributeService attributeService;

    public AuthorizationAttributeAssigmentDialog(AuthService authService,
                                                 AttributeService attributeService,
                                                 RadiusUserService userService,
                                                 AuthFormConfiguration formConfig,
                                                 CreationListener<Void> creationListener) {
        super(userService, formConfig, creationListener);
        this.authService = authService;
        this.attributeService = attributeService;
    }

    @Override
    String getDialogTitle() {
        return "Assign authorization attribute";
    }

    @Override
    Class<AuthorizationDto> getClazz() {
        return AuthorizationDto.class;
    }

    @Override
    AuthorizationDto getNewBeanInstance() {
        return new AuthorizationDto();
    }

    @Override
    long countAttributes(AttributeFilter filter) {
        return attributeService.countAuthorizationAttributeRecords(filter);
    }

    @Override
    Page<AuthorizationAttributeDto> pageAttributes(AttributeFilter filter, Pageable pageable) {
        return attributeService.pageAuthorizationAttributeRecords(filter, pageable);
    }

    @Override
    void assignAuth(AuthorizationDto authDto) {
        authService.createAuthorization(authDto);
    }

}
