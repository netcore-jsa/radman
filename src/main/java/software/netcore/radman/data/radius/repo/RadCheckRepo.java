package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadCheck;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadCheckRepo extends RadiusRepository<RadCheck> {

    List<RadCheck> findAll();

    void deleteAllByUsername(String username);

    void deleteByUsernameAndAttribute(String name, String attribute);

}
