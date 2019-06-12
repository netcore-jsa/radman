package software.netcore.radman.data.radius.repo;

import org.springframework.data.jpa.repository.Query;
import software.netcore.radman.data.radius.entity.RadCheck;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;
import java.util.Set;

/**
 * @since v. 1.0.0
 */
public interface RadCheckRepo extends RadiusRepository<RadCheck> {

    List<RadCheck> findAll();

    void deleteAllByUsername(String username);

    void deleteByUsernameAndAttribute(String name, String attribute);

    void deleteAllByAttribute(String attribute);

    @Query("SELECT r.username FROM RadCheck r ORDER BY r.username")
    Set<String> getUsernames();

    @Query("SELECT r.attribute FROM RadCheck r ORDER BY r.attribute")
    Set<String> getAttributes();

}
