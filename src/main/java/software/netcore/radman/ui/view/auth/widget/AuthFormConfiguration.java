package software.netcore.radman.ui.view.auth.widget;

import lombok.AllArgsConstructor;
import lombok.Getter;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;

@Getter
@AllArgsConstructor
public class AuthFormConfiguration {

    private final boolean showFullForm;

    private final boolean predefinedUser;

    private final boolean predefinedAttrTargetAsUser;

    private final boolean predefinedAttrTargetAsGroup;

    private final RadiusUserDto predefinedUserDto;

}
