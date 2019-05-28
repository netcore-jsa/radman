package software.netcore.radman.data.internal.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.internal.entity.RadiusGroup;
import software.netcore.radman.data.internal.entity.RadiusUser;
import software.netcore.radman.data.spec.BaseRepository;

/**
 * @since v. 1.0.0
 */
public interface RadiusUserRepo extends BaseRepository<RadiusUser, Long> {

    @Query("SELECT r FROM RadiusUser r")
    Page<RadiusUser> pageRadiusUsers(Pageable pageable);

}
