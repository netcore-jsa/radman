package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadHuntGroupRepo extends BaseRepository<RadHuntGroup, Integer> {

    List<RadHuntGroup> findByNasIpAddress(String nasIpAddress);

}
