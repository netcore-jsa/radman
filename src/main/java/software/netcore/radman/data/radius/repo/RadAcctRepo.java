package software.netcore.radman.data.radius.repo;

import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.radius.entity.RadAcct;

/**
 * @since v. 1.0.0
 */
public interface RadAcctRepo extends CrudRepository<RadAcct, Long> {
}
