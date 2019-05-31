package software.netcore.radman.buisness.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.auth.dto.*;
import software.netcore.radman.data.internal.entity.RadCheckAttribute;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;
import software.netcore.radman.data.internal.repo.RadCheckAttributeRepo;
import software.netcore.radman.data.internal.repo.RadReplyAttributeRepo;
import software.netcore.radman.data.radius.entity.RadCheck;
import software.netcore.radman.data.radius.entity.RadGroupCheck;
import software.netcore.radman.data.radius.entity.RadGroupReply;
import software.netcore.radman.data.radius.entity.RadReply;
import software.netcore.radman.data.radius.repo.RadCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupReplyRepo;
import software.netcore.radman.data.radius.repo.RadReplyRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class AuthService {

    // radius
    private final RadCheckRepo radCheckRepo;
    private final RadReplyRepo radReplyRepo;
    private final RadGroupCheckRepo radGroupCheckRepo;
    private final RadGroupReplyRepo radGroupReplyRepo;

    // internal
    private final RadCheckAttributeRepo radCheckAttributeRepo;
    private final RadReplyAttributeRepo radReplyAttributeRepo;

    private final ConversionService conversionService;

    public void createAuthentication(AuthenticationDto authenticationDto) {
        if (authenticationDto.getAuthTarget() == AuthTarget.RADIUS_USER) {
            RadCheck radCheck = conversionService.convert(authenticationDto, RadCheck.class);
            radCheckRepo.save(radCheck);
        } else {
            RadGroupCheck radGroupCheck = conversionService.convert(authenticationDto, RadGroupCheck.class);
            radGroupCheckRepo.save(radGroupCheck);
        }
    }

    public void createAuthorization(AuthorizationDto authorizationDto) {
        if (authorizationDto.getAuthTarget() == AuthTarget.RADIUS_USER) {
            RadReply radReply = conversionService.convert(authorizationDto, RadReply.class);
            radReplyRepo.save(radReply);
        } else {
            RadGroupReply radGroupReply = conversionService.convert(authorizationDto, RadGroupReply.class);
            radGroupReplyRepo.save(radGroupReply);
        }
    }

    public AuthorizationsDto getAuthorizations() {
        Map<String, String> columnsSpec = initCommonColumnsSpec();

        List<RadReply> radReplies = radReplyRepo.findAll();
        Map<String, List<RadReply>> radReplyMap = new HashMap<>();
        for (RadReply radReply : radReplies) {
            radReplyMap.putIfAbsent(radReply.getAttribute(), new ArrayList<>());
            radReplyMap.get(radReply.getAttribute()).add(radReply);
        }

        List<RadGroupReply> radGroupReplies = radGroupReplyRepo.findAll();
        Map<String, List<RadGroupReply>> radGroupCheckMap = new HashMap<>();
        for (RadGroupReply radGroupReply : radGroupReplies) {
            radGroupCheckMap.putIfAbsent(radGroupReply.getAttribute(), new ArrayList<>());
            radGroupCheckMap.get(radGroupReply.getAttribute()).add(radGroupReply);
        }

        Map<String, Map<String, String>> usersData = new HashMap<>();
        Map<String, Map<String, String>> groupsData = new HashMap<>();

        List<RadReplyAttribute> radReplyAttributes = radReplyAttributeRepo.findAll();
        for (RadReplyAttribute radReplyAttribute : radReplyAttributes) {
            columnsSpec.put(radReplyAttribute.getName(), StringUtils.capitalize(radReplyAttribute.getName()));

            if (radReplyMap.containsKey(radReplyAttribute.getName())) {
                List<RadReply> attrRadReplies = radReplyMap.get(radReplyAttribute.getName());
                for (RadReply attrRadReply : attrRadReplies) {
                    String key = attrRadReply.getUsername();
                    Map<String, String> singleUserData = initDefaultRowDataIfRequired(key,
                            AuthTarget.RADIUS_USER.getValue(), usersData);
                    singleUserData.put(radReplyAttribute.getName(),
                            attrRadReply.getOp() + " " + attrRadReply.getValue());
                }
            }

            if (radGroupCheckMap.containsKey(radReplyAttribute.getName())) {
                List<RadGroupReply> attrRadGroupReplies = radGroupCheckMap.get(radReplyAttribute.getName());
                for (RadGroupReply attrRadGroupCheck : attrRadGroupReplies) {
                    String key = attrRadGroupCheck.getGroupName();
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(key,
                            AuthTarget.RADIUS_GROUP.getValue(), groupsData);
                    singleGroupData.put(radReplyAttribute.getName(),
                            attrRadGroupCheck.getOp() + " " + attrRadGroupCheck.getValue());
                }
            }
        }

        List<Map<String, String>> data = new ArrayList<>();
        data.addAll(usersData.values());
        data.addAll(groupsData.values());
        return new AuthorizationsDto(columnsSpec, data);
    }


    public AuthenticationsDto getAuthentications() {
        Map<String, String> columnsSpec = initCommonColumnsSpec();

        List<RadCheck> radChecks = radCheckRepo.findAll();
        Map<String, List<RadCheck>> radCheckMap = new HashMap<>();
        for (RadCheck radCheck : radChecks) {
            radCheckMap.putIfAbsent(radCheck.getAttribute(), new ArrayList<>());
            radCheckMap.get(radCheck.getAttribute()).add(radCheck);
        }

        List<RadGroupCheck> radGroupChecks = radGroupCheckRepo.findAll();
        Map<String, List<RadGroupCheck>> radGroupCheckMap = new HashMap<>();
        for (RadGroupCheck radGroupCheck : radGroupChecks) {
            radGroupCheckMap.putIfAbsent(radGroupCheck.getAttribute(), new ArrayList<>());
            radGroupCheckMap.get(radGroupCheck.getAttribute()).add(radGroupCheck);
        }

        Map<String, Map<String, String>> usersData = new HashMap<>();
        Map<String, Map<String, String>> groupsData = new HashMap<>();

        List<RadCheckAttribute> radCheckAttributes = radCheckAttributeRepo.findAll();
        for (RadCheckAttribute radCheckAttribute : radCheckAttributes) {
            columnsSpec.put(radCheckAttribute.getName(), StringUtils.capitalize(radCheckAttribute.getName()));

            if (radCheckMap.containsKey(radCheckAttribute.getName())) {
                List<RadCheck> attrRadChecks = radCheckMap.get(radCheckAttribute.getName());
                for (RadCheck attrRadCheck : attrRadChecks) {
                    String key = attrRadCheck.getUsername();
                    Map<String, String> singleUserData = initDefaultRowDataIfRequired(key,
                            AuthTarget.RADIUS_USER.getValue(), usersData);
                    singleUserData.put(radCheckAttribute.getName(),
                            attrRadCheck.getOp() + " " + attrRadCheck.getValue());
                }
            }

            if (radGroupCheckMap.containsKey(radCheckAttribute.getName())) {
                List<RadGroupCheck> attrRadGroupChecks = radGroupCheckMap.get(radCheckAttribute.getName());
                for (RadGroupCheck attrRadGroupCheck : attrRadGroupChecks) {
                    String key = attrRadGroupCheck.getGroupName();
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(key,
                            AuthTarget.RADIUS_GROUP.getValue(), groupsData);
                    singleGroupData.put(radCheckAttribute.getName(),
                            attrRadGroupCheck.getOp() + " " + attrRadGroupCheck.getValue());
                }
            }
        }

        List<Map<String, String>> data = new ArrayList<>();
        data.addAll(usersData.values());
        data.addAll(groupsData.values());
        return new AuthenticationsDto(columnsSpec, data);
    }

    private Map<String, String> initCommonColumnsSpec() {
        Map<String, String> columnsSpec = new HashMap<>();
        columnsSpec.put("name", "Name");
        columnsSpec.put("type", "Type");
        return columnsSpec;
    }

    private Map<String, String> initDefaultRowDataIfRequired(String key, String type,
                                                             Map<String, Map<String, String>> data) {
        Map<String, String> singleData;
        if (!data.containsKey(key)) { // inits user data if non exists
            singleData = new HashMap<>();
            data.put(key, singleData);
            singleData.put("name", key);
            singleData.put("type", type);
        } else {
            singleData = data.get(key);
        }
        return singleData;
    }

}
