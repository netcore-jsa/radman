package software.netcore.radman.data.internal.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.internal.entity.RadCheckAttribute;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadCheckAttributeRepo extends CrudRepository<RadCheckAttribute, Long> {

    List<RadCheckAttribute> findAll();

    @Query("SELECT a FROM RadCheckAttribute a")
    Page<RadCheckAttribute> pageCheckAttributes(Pageable pageable);

}
