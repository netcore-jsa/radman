package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadGroupReply;
import software.netcore.radman.data.radius.spec.RadiusRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadGroupReplyRepo extends RadiusRepository<RadGroupReply> {

    List<RadGroupReply> findAll();

    void deleteAllByGroupName(String name);

    void deleteAllByGroupNameAndAttribute(String name, String attribute);

}
