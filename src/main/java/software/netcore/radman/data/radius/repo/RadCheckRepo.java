package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadCheck;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadCheckRepo extends BaseRepository<RadCheck, Integer> {

    List<RadCheck> findAll();

}
