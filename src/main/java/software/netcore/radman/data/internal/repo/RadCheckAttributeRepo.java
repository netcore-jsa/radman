package software.netcore.radman.data.internal.repo;

import software.netcore.radman.data.internal.entity.RadCheckAttribute;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadCheckAttributeRepo extends BaseRepository<RadCheckAttribute, Long> {

    List<RadCheckAttribute> findAll();

}
