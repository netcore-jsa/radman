package software.netcore.radman.data.spec;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * @param <T>
 * @param <ID>
 * @since v. 1.0.0
 */
public interface BaseRepository<T, ID> extends CrudRepository<T, ID>, QuerydslPredicateExecutor<T> {
}
