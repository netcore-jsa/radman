package software.netcore.radman.data.internal.repo;

import software.netcore.radman.data.internal.entity.RadCheckAttribute;
import software.netcore.radman.data.internal.spec.RadmanRepository;

import java.util.List;

/**
 * @since v. 1.0.0
 */
public interface RadCheckAttributeRepo extends RadmanRepository<RadCheckAttribute> {

    List<RadCheckAttribute> findAll();

}
