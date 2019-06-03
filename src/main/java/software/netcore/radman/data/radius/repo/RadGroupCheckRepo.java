package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadGroupCheck;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadGroupCheckRepo extends BaseRepository<RadGroupCheck, Integer> {

    List<RadGroupCheck> findAll();

    void deleteByGroupName(String name);

}
