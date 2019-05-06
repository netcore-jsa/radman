package software.netcore.radman.data.radius.repo;

import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.radius.entity.RadCheck;

/**
 * @since v. 1.0.0
 */
public interface RadCheckRepo extends CrudRepository<RadCheck, Integer> {
}
