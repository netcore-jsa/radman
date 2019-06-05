package software.netcore.radman.security.ldap;

import lombok.Getter;
import lombok.Setter;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
public class LdapProperties {

    private String ldapUrls;
    private String ldapBaseDn;
    private String ldapSecurityPrincipal;
    private String ldapPrincipalPassword;
    private String ldapUserDnPattern;
    private boolean ldapEnabled;

}
