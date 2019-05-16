package software.netcore.radman.buisness.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import software.netcore.radman.buisness.service.auth.dto.AuthenticationDto;
import software.netcore.radman.buisness.service.auth.dto.AuthorizationDto;
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

    private static final String USER_TYPE = "user";
    private static final String GROUP_TYPE = "group";

    // radius
    private final RadCheckRepo radCheckRepo;
    private final RadReplyRepo radReplyRepo;
    private final RadGroupCheckRepo radGroupCheckRepo;
    private final RadGroupReplyRepo radGroupReplyRepo;

    // internal
    private final RadCheckAttributeRepo radCheckAttributeRepo;
    private final RadReplyAttributeRepo radReplyAttributeRepo;

    public AuthenticationDto getAuthentications() {
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
                    Map<String, String> singleUserData = initDefaultRowDataIfRequired(key, USER_TYPE, usersData);
                    singleUserData.put(radReplyAttribute.getName(),
                            attrRadReply.getOp() + " " + attrRadReply.getValue());
                }
            }

            if (radGroupCheckMap.containsKey(radReplyAttribute.getName())) {
                List<RadGroupReply> attrRadGroupReplies = radGroupCheckMap.get(radReplyAttribute.getName());
                for (RadGroupReply attrRadGroupCheck : attrRadGroupReplies) {
                    String key = attrRadGroupCheck.getGroupName();
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(key, GROUP_TYPE, groupsData);
                    singleGroupData.put(radReplyAttribute.getName(),
                            attrRadGroupCheck.getOp() + " " + attrRadGroupCheck.getValue());
                }
            }
        }

        List<Map<String, String>> data = new ArrayList<>();
        data.addAll(usersData.values());
        data.addAll(groupsData.values());
        return new AuthenticationDto(columnsSpec, data);
    }


    public AuthorizationDto getAuthorizations() {
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
                    Map<String, String> singleUserData = initDefaultRowDataIfRequired(key, USER_TYPE, usersData);
                    singleUserData.put(radCheckAttribute.getName(),
                            attrRadCheck.getOp() + " " + attrRadCheck.getValue());
                }
            }

            if (radGroupCheckMap.containsKey(radCheckAttribute.getName())) {
                List<RadGroupCheck> attrRadGroupChecks = radGroupCheckMap.get(radCheckAttribute.getName());
                for (RadGroupCheck attrRadGroupCheck : attrRadGroupChecks) {
                    String key = attrRadGroupCheck.getGroupName();
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(key, GROUP_TYPE, groupsData);
                    singleGroupData.put(radCheckAttribute.getName(),
                            attrRadGroupCheck.getOp() + " " + attrRadGroupCheck.getValue());
                }
            }
        }

        List<Map<String, String>> data = new ArrayList<>();
        data.addAll(usersData.values());
        data.addAll(groupsData.values());
        return new AuthorizationDto(columnsSpec, data);
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
