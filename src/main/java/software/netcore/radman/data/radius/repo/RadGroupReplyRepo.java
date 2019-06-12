package software.netcore.radman.data.radius.repo;

import org.springframework.data.jpa.repository.Query;
import software.netcore.radman.data.radius.entity.RadGroupReply;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;
import java.util.Set;

/**
 * @since v. 1.0.0
 */
public interface RadGroupReplyRepo extends RadiusRepository<RadGroupReply> {

    List<RadGroupReply> findAll();

    void deleteAllByGroupName(String name);

    void deleteAllByGroupNameAndAttribute(String name, String attribute);

    void deleteAllByAttribute(String attribute);

    @Query("SELECT r.groupName FROM RadGroupReply r ORDER BY r.groupName")
    Set<String> getGroupNames();

    @Query("SELECT r.attribute FROM RadGroupReply r ORDER BY r.attribute")
    Set<String> getAttributes();

}
