package software.netcore.radman.data.internal.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.internal.entity.SystemUser;

/**
 * @since v. 1.0.0
 */
public interface SystemUserRepo extends CrudRepository<SystemUser, Long> {

    @Query("SELECT COUNT(s) FROM SystemUser s")
    long countSystemUsers();

    @Query("SELECT s FROM SystemUser s")
    Page<SystemUser> pageSystemUsers(Pageable pageable);

}
