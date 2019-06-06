package software.netcore.radman.data.internal.repo;

import software.netcore.radman.data.internal.entity.AuthProvider;
import software.netcore.radman.data.internal.entity.Role;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.internal.spec.RadmanRepository;

/**
 * @since v. 1.0.0
 */
public interface SystemUserRepo extends RadmanRepository<SystemUser> {

    SystemUser findByUsername(String username);

    long countByRoleAndAuthProvider(Role role, AuthProvider authProvider);

}
