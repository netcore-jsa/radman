package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadHuntGroupRepo extends RadiusRepository<RadHuntGroup, Integer> {

    boolean existsByNasIpAddress(String ipAddress);

}
