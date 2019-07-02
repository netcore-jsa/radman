package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.spec.RadiusRepository;

/**
 * @since v. 1.0.0
 */
public interface NasRepo extends RadiusRepository<Nas, Integer> {

    boolean existsByNasName(String name);

}
