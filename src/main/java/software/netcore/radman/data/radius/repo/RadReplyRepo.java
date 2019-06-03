package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadReply;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadReplyRepo extends BaseRepository<RadReply, Integer> {

    List<RadReply> findAll();

    void deleteByUsername(String name);

}
