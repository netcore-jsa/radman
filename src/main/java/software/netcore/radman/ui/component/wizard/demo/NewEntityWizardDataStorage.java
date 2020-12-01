package software.netcore.radman.ui.component.wizard.demo;

import lombok.Getter;
import lombok.Setter;
import software.netcore.radman.buisness.service.attribute.dto.AuthenticationAttributeDto;
import software.netcore.radman.buisness.service.attribute.dto.AuthorizationAttributeDto;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
import software.netcore.radman.buisness.service.nas.dto.NasDto;
import software.netcore.radman.buisness.service.nas.dto.NasGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusGroupDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserDto;
import software.netcore.radman.buisness.service.user.radius.dto.RadiusUserToGroupDto;
import software.netcore.radman.ui.component.wizard.DataStorage;

import java.util.HashSet;
import java.util.Set;

/**
 * @author daniel
 * @since v. 1.0.3
 */
@Getter
@Setter
public class NewEntityWizardDataStorage implements DataStorage {

    private NasDto nasDto;

    private Set<NasGroupDto> nasGroupDtos = new HashSet<>();

    private Set<RadiusGroupDto> radiusGroupDtos = new HashSet<>();

    private RadiusUserDto radiusUserDto;

    private Set<RadiusUserToGroupDto> radiusUserToGroupDtos = new HashSet<>();

    private AuthenticationAttributeDto authenticationAttributeDto;

    private AuthorizationAttributeDto authorizationAttributeDto;

    private Set<AuthenticationDto> authenticationDto = new HashSet<>();

    private Set<AuthorizationDto> authorizationDto = new HashSet<>();

}
