package software.netcore.radman.data.radius.repo;

import software.netcore.radman.data.radius.entity.RadGroupReply;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadGroupReplyRepo extends BaseRepository<RadGroupReply, Integer> {

    List<RadGroupReply> findAll();

}
