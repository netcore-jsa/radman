package software.netcore.radman.data.radius.spec;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import software.netcore.radman.data.spec.BaseRepository;

/**
 * @param <T>
 * @since v. 1.0.0
 */
@NoRepositoryBean
@Transactional(transactionManager = "txRadius")
public interface RadiusRepository<T, ID> extends BaseRepository<T, ID> {
}
