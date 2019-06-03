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

import java.util.*;

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

    public void deleteAuthentication(String name, String type) {
        AuthTarget authTarget = AuthTarget.fromValue(type);
        if (authTarget == AuthTarget.RADIUS_USER) {
            radCheckRepo.deleteByUsername(name);
        } else {
            radGroupCheckRepo.deleteByGroupName(name);
        }
    }

    public void deleteAuthorization(String name, String type) {
        AuthTarget authTarget = AuthTarget.fromValue(type);
        if (authTarget == AuthTarget.RADIUS_USER) {
            radReplyRepo.deleteByUsername(name);
        } else {
            radGroupReplyRepo.deleteByGroupName(name);
        }
    }

    @SuppressWarnings("Duplicates")
    public AuthorizationsDto getAuthorizations() {
        Map<String, String> columnsSpec = initCommonColumnsSpec();

        List<RadReply> radReplies = radReplyRepo.findAll();
        Map<String, List<RadReply>> radReplyMap = new HashMap<>();
        for (RadReply radReply : radReplies) {
            radReplyMap.putIfAbsent(radReply.getAttribute(), new ArrayList<>());
            radReplyMap.get(radReply.getAttribute()).add(radReply);
        }

        List<RadGroupReply> radGroupReplies = radGroupReplyRepo.findAll();
        Map<String, List<RadGroupReply>> radGroupReplyMap = new HashMap<>();
        for (RadGroupReply radGroupReply : radGroupReplies) {
            radGroupReplyMap.putIfAbsent(radGroupReply.getAttribute(), new ArrayList<>());
            radGroupReplyMap.get(radGroupReply.getAttribute()).add(radGroupReply);
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
                    String attrValue = radReplyAttribute.isSensitiveData() ?
                            attrRadReply.getValue().replaceAll(".", "*")
                            : attrRadReply.getValue();
                    singleUserData.put(radReplyAttribute.getName(), attrRadReply.getOp() + " " + attrValue);
                }
            }

            if (radGroupReplyMap.containsKey(radReplyAttribute.getName())) {
                List<RadGroupReply> attrRadGroupReplies = radGroupReplyMap.get(radReplyAttribute.getName());
                for (RadGroupReply attrRadGroupReply : attrRadGroupReplies) {
                    String key = attrRadGroupReply.getGroupName();
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(key,
                            AuthTarget.RADIUS_GROUP.getValue(), groupsData);
                    String attrValue = radReplyAttribute.isSensitiveData() ?
                            attrRadGroupReply.getValue().replaceAll(".", "*")
                            : attrRadGroupReply.getValue();
                    singleGroupData.put(radReplyAttribute.getName(), attrRadGroupReply.getOp() + " " + attrValue);
                }
            }
        }

        List<Map<String, String>> data = new ArrayList<>();
        data.addAll(usersData.values());
        data.addAll(groupsData.values());
        return new AuthorizationsDto(columnsSpec, data);
    }


    @SuppressWarnings("Duplicates")
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
                    String attrValue = radCheckAttribute.isSensitiveData() ?
                            attrRadCheck.getValue().replaceAll(".", "*") :
                            attrRadCheck.getValue();
                    singleUserData.put(radCheckAttribute.getName(), attrRadCheck.getOp() + " " + attrValue);
                }
            }

            if (radGroupCheckMap.containsKey(radCheckAttribute.getName())) {
                List<RadGroupCheck> attrRadGroupChecks = radGroupCheckMap.get(radCheckAttribute.getName());
                for (RadGroupCheck attrRadGroupCheck : attrRadGroupChecks) {
                    String key = attrRadGroupCheck.getGroupName();
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(key,
                            AuthTarget.RADIUS_GROUP.getValue(), groupsData);
                    String attrValue = radCheckAttribute.isSensitiveData() ?
                            attrRadGroupCheck.getValue().replaceAll(".", "*")
                            : attrRadGroupCheck.getValue();
                    singleGroupData.put(radCheckAttribute.getName(), attrRadGroupCheck.getOp() + " " + attrValue);
                }
            }
        }

        List<Map<String, String>> data = new ArrayList<>();
        data.addAll(usersData.values());
        data.addAll(groupsData.values());
        return new AuthenticationsDto(columnsSpec, data);
    }

    private Map<String, String> initCommonColumnsSpec() {
        Map<String, String> columnsSpec = new LinkedHashMap<>();
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
