package software.netcore.radman.data.radius.repo;

import org.springframework.data.jpa.repository.Query;
import software.netcore.radman.data.radius.entity.RadReply;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;
import java.util.Set;

/**
 * @since v. 1.0.0
 */
public interface RadReplyRepo extends RadiusRepository<RadReply, Integer> {

    List<RadReply> findAll();

    void deleteAllByUsername(String username);

    void deleteByUsernameAndAttribute(String name, String attribute);

    void deleteAllByAttribute(String attribute);

    @Query("SELECT r.username FROM RadReply r ORDER BY r.username")
    Set<String> getUsernames();

    @Query("SELECT r.attribute FROM RadReply r ORDER BY r.attribute")
    Set<String> getAttributes();

}
