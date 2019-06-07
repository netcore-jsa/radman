package software.netcore.radman.security.ldap;

import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import software.netcore.radman.data.internal.entity.SystemUser;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.RoleAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @since v. 1.0.0
 */
@RequiredArgsConstructor
public class LocalLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private final SystemUserRepo systemUserRepo;

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        SystemUser systemUser = systemUserRepo.findByUsername(username);
        if (Objects.isNull(systemUser)) {
            return Collections.emptyList();
        }
        return RoleAuthority.asCollection(new RoleAuthority(systemUser.getRole()));
    }

}
