package software.netcore.radman.data.internal.repo;

import software.netcore.radman.data.internal.entity.RadReplyAttribute;
import software.netcore.radman.data.spec.BaseRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadReplyAttributeRepo extends BaseRepository<RadReplyAttribute, Long> {

    List<RadReplyAttribute> findAll();

}
