package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadUserGroup;
import software.netcore.radman.data.radius.spec.RadiusRepository;

/**
 * @since v. 1.0.0
 */
public interface RadUserGroupRepo extends RadiusRepository<RadUserGroup, Integer> {

    void deleteAllByGroupName(String groupName);

    void deleteAllByUsername(String username);

}
