package software.netcore.radman.buisness.service.auth;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
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
import software.netcore.radman.ui.support.Filter;

import java.util.*;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class AuthService {

    private static final String NAME_COLUMN_KEY = "name";
    private static final String TYPE_COLUMN_KEY = "type";

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
            radCheckRepo.deleteByUsernameAndAttribute(radCheck.getUsername(), radCheck.getAttribute());
            radCheckRepo.save(radCheck);
        } else {
            RadGroupCheck radGroupCheck = conversionService.convert(authenticationDto, RadGroupCheck.class);
            radGroupCheckRepo.deleteAllByGroupNameAndAttribute(radGroupCheck.getGroupName(),
                    radGroupCheck.getAttribute());
            radGroupCheckRepo.save(radGroupCheck);
        }
    }

    public void createAuthorization(AuthorizationDto authorizationDto) {
        if (authorizationDto.getAuthTarget() == AuthTarget.RADIUS_USER) {
            RadReply radReply = conversionService.convert(authorizationDto, RadReply.class);
            radReplyRepo.deleteByUsernameAndAttribute(radReply.getUsername(), radReply.getAttribute());
            radReplyRepo.save(radReply);
        } else {
            RadGroupReply radGroupReply = conversionService.convert(authorizationDto, RadGroupReply.class);
            radGroupReplyRepo.deleteAllByGroupNameAndAttribute(radGroupReply.getGroupName(),
                    radGroupReply.getAttribute());
            radGroupReplyRepo.save(radGroupReply);
        }
    }

    public void deleteAuthentication(String name, String type) {
        AuthTarget authTarget = AuthTarget.fromValue(type);
        if (authTarget == AuthTarget.RADIUS_USER) {
            radCheckRepo.deleteAllByUsername(name);
        } else {
            radGroupCheckRepo.deleteAllByGroupName(name);
        }
    }

    public void deleteAuthorization(String name, String type) {
        AuthTarget authTarget = AuthTarget.fromValue(type);
        if (authTarget == AuthTarget.RADIUS_USER) {
            radReplyRepo.deleteAllByUsername(name);
        } else {
            radGroupReplyRepo.deleteAllByGroupName(name);
        }
    }

    @SuppressWarnings("Duplicates")
    public AuthorizationsDto getAuthorizations(Filter filter) {
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
        Map<String, RadReplyAttribute> repliesAttrMapping = new HashMap<>();
        for (RadReplyAttribute radReplyAttribute : radReplyAttributes) {
            repliesAttrMapping.put(radReplyAttribute.getName(), radReplyAttribute);
            columnsSpec.put(radReplyAttribute.getName(), StringUtils.capitalize(radReplyAttribute.getName()));

            if (radReplyMap.containsKey(radReplyAttribute.getName())) {
                List<RadReply> attrRadReplies = radReplyMap.get(radReplyAttribute.getName());
                for (RadReply attrRadReply : attrRadReplies) {
                    Map<String, String> singleUserData = initDefaultRowDataIfRequired(attrRadReply.getUsername(),
                            AuthTarget.RADIUS_USER.getValue(), usersData);
                    String attrValue = radReplyAttribute.isSensitiveData() ?
                            attrRadReply.getValue().replaceAll(".", "*") : attrRadReply.getValue();
                    String finalValue = attrRadReply.getOp() + " " + attrValue;
                    singleUserData.put(radReplyAttribute.getName(), finalValue);
                }
            }

            if (radGroupReplyMap.containsKey(radReplyAttribute.getName())) {
                List<RadGroupReply> attrRadGroupReplies = radGroupReplyMap.get(radReplyAttribute.getName());
                for (RadGroupReply attrRadGroupReply : attrRadGroupReplies) {
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(attrRadGroupReply.getGroupName(),
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

        // apply filter
        if (!StringUtils.isEmpty(filter.getSearchText())) {
            Iterator<Map<String, String>> iterator = data.iterator();
            while (iterator.hasNext()) {
                Map<String, String> row = iterator.next();
                boolean pass = false;
                for (String key : row.keySet()) {
                    if (Objects.equals(NAME_COLUMN_KEY, key) || Objects.equals(TYPE_COLUMN_KEY, key)) {
                        String opValue = row.get(key);
                        if (Objects.nonNull(opValue)) {
                            pass = pass || StringUtils.contains(opValue, filter.getSearchText());
                        }
                    } else {
                        RadReplyAttribute radReplyAttribute = repliesAttrMapping.get(key);
                        if (!radReplyAttribute.isSensitiveData()) {
                            String opValue = row.get(key);
                            if (Objects.nonNull(opValue)) {
                                pass = pass || StringUtils.contains(opValue, filter.getSearchText());
                            }
                        }
                    }
                }
                if (!pass) {
                    iterator.remove();
                }
            }
        }

        return new AuthorizationsDto(columnsSpec, data);
    }


    @SuppressWarnings("Duplicates")
    public AuthenticationsDto getAuthentications(Filter filter) {
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
        Map<String, RadCheckAttribute> checksAttrMapping = new HashMap<>();
        for (RadCheckAttribute radCheckAttribute : radCheckAttributes) {
            checksAttrMapping.put(radCheckAttribute.getName(), radCheckAttribute);
            columnsSpec.put(radCheckAttribute.getName(), StringUtils.capitalize(radCheckAttribute.getName()));

            if (radCheckMap.containsKey(radCheckAttribute.getName())) {
                List<RadCheck> attrRadChecks = radCheckMap.get(radCheckAttribute.getName());
                for (RadCheck attrRadCheck : attrRadChecks) {
                    Map<String, String> singleUserData = initDefaultRowDataIfRequired(attrRadCheck.getUsername(),
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
                    Map<String, String> singleGroupData = initDefaultRowDataIfRequired(attrRadGroupCheck.getGroupName(),
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

        // apply filter
        if (!StringUtils.isEmpty(filter.getSearchText())) {
            Iterator<Map<String, String>> iterator = data.iterator();
            while (iterator.hasNext()) {
                Map<String, String> row = iterator.next();
                boolean pass = false;
                for (String key : row.keySet()) {
                    if (Objects.equals(NAME_COLUMN_KEY, key) || Objects.equals(TYPE_COLUMN_KEY, key)) {
                        String opValue = row.get(key);
                        if (Objects.nonNull(opValue)) {
                            pass = pass || StringUtils.contains(opValue, filter.getSearchText());
                        }
                    } else {
                        RadCheckAttribute radReplyAttribute = checksAttrMapping.get(key);
                        if (!radReplyAttribute.isSensitiveData()) {
                            String opValue = row.get(key);
                            if (Objects.nonNull(opValue)) {
                                pass = pass || StringUtils.contains(opValue, filter.getSearchText());
                            }
                        }
                    }
                }
                if (!pass) {
                    iterator.remove();
                }
            }
        }

        return new AuthenticationsDto(columnsSpec, data);
    }

    private Map<String, String> initCommonColumnsSpec() {
        Map<String, String> columnsSpec = new LinkedHashMap<>();
        columnsSpec.put(NAME_COLUMN_KEY, "Name");
        columnsSpec.put(TYPE_COLUMN_KEY, "Type");
        return columnsSpec;
    }

    private Map<String, String> initDefaultRowDataIfRequired(String name, String type,
                                                             Map<String, Map<String, String>> data) {
        String key = name + ":" + type;
        Map<String, String> singleData;
        if (!data.containsKey(key)) { // inits user data if non exists
            singleData = new HashMap<>();
            data.put(key, singleData);
            singleData.put(NAME_COLUMN_KEY, name);
            singleData.put(TYPE_COLUMN_KEY, type);
        } else {
            singleData = data.get(key);
        }
        return singleData;
    }

}
