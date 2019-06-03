package software.netcore.radman.data.internal.spec;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import software.netcore.radman.data.spec.BaseRepository;

/**
 * @param <T>
 * @since v. 1.0.0
 */
@NoRepositoryBean
@Transactional("txRadman")
public interface RadmanRepository<T> extends BaseRepository<T, Long> {
}
