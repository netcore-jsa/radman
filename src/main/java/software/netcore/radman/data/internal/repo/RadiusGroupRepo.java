package software.netcore.radman.data.internal.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import software.netcore.radman.data.internal.entity.RadiusGroup;
import software.netcore.radman.data.spec.BaseRepository;

/**
 * @since v. 1.0.0
 */
public interface RadiusGroupRepo extends BaseRepository<RadiusGroup, Long> {

    @Query("SELECT r FROM RadiusGroup r")
    Page<RadiusGroup> pageRadiusUsersGroups(Pageable pageable);

}
