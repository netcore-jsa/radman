package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadReply;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadReplyRepo extends RadiusRepository<RadReply> {

    List<RadReply> findAll();

    void deleteAllByUsername(String username);

    void deleteByUsernameAndAttribute(String name, String attribute);

}
