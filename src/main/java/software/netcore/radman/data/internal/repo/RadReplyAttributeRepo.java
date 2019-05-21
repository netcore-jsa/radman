package software.netcore.radman.data.internal.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.internal.entity.RadReplyAttribute;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadReplyAttributeRepo extends CrudRepository<RadReplyAttribute, Long> {

    List<RadReplyAttribute> findAll();

    @Query("SELECT a FROM RadReplyAttribute a")
    Page<RadReplyAttribute> pageReplyAttributes(Pageable pageable);

}
