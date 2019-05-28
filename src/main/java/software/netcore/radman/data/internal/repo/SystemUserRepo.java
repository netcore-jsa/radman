package software.netcore.radman.data.internal.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.spec.BaseRepository;

/**
 * @since v. 1.0.0
 */
public interface SystemUserRepo extends BaseRepository<SystemUser, Long> {

    @Query("SELECT COUNT(s) FROM SystemUser s")
    long countSystemUsers();

    @Query("SELECT s FROM SystemUser s")
    Page<SystemUser> pageSystemUsers(Pageable pageable);

}
