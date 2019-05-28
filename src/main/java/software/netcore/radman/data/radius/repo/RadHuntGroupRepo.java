package software.netcore.radman.data.radius.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import software.netcore.radman.data.radius.entity.Nas;
import software.netcore.radman.data.radius.entity.RadHuntGroup;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadHuntGroupRepo extends BaseRepository<RadHuntGroup, Integer> {

    @Query("SELECT r FROM RadHuntGroup r")
    Page<RadHuntGroup> pageRadHuntGroupRecords(Pageable pageable);

    List<RadHuntGroup> findByNasIpAddress(String nasIpAddress);

}
