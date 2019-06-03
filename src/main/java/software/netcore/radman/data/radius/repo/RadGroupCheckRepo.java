package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadGroupCheck;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadGroupCheckRepo extends RadiusRepository<RadGroupCheck> {

    List<RadGroupCheck> findAll();

    void deleteAllByGroupName(String groupName);

    void deleteAllByGroupNameAndAttribute(String name, String attribute);

}
