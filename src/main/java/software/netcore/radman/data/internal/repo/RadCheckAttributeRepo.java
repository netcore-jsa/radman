package software.netcore.radman.data.internal.repo;

import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.internal.entity.RadCheckAttribute;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadCheckAttributeRepo extends CrudRepository<RadCheckAttribute, Long> {

    List<RadCheckAttribute> findAll();

}
