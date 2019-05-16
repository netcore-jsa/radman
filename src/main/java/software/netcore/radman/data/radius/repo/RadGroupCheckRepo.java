package software.netcore.radman.data.radius.repo;

import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.radius.entity.RadGroupCheck;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadGroupCheckRepo extends CrudRepository<RadGroupCheck, Integer> {

    List<RadGroupCheck> findAll();

}
