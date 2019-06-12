package software.netcore.radman.data.radius.repo;

import org.springframework.data.jpa.repository.Query;
import software.netcore.radman.data.radius.entity.RadGroupCheck;
import software.netcore.radman.data.radius.entity.RadReply;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;
import java.util.Set;

/**
 * @since v. 1.0.0
 */
public interface RadGroupCheckRepo extends RadiusRepository<RadGroupCheck> {

    List<RadGroupCheck> findAll();

    void deleteAllByGroupName(String groupName);

    void deleteAllByGroupNameAndAttribute(String name, String attribute);

    void deleteAllByAttribute(String attribute);

    @Query("SELECT r.groupName FROM RadGroupCheck r ORDER BY r.groupName")
    Set<String> getGroupNames();

    @Query("SELECT r.attribute FROM RadGroupCheck r ORDER BY r.attribute")
    Set<String> getAttributes();

}
