package software.netcore.radman.security.ldap;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class LdapProperties {

    private boolean enabled;
    private String urls;
    private String managerDn;
    private String managerPassword;
    private String searchBaseDn;
    private String userSearchFilter;

}
