package software.netcore.radman.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import software.netcore.radman.data.internal.repo.SystemUserRepo;
import software.netcore.radman.security.fallback.FallbackAuthenticationProvider;
import software.netcore.radman.security.fallback.SingleUserDetailsManager;
import software.netcore.radman.security.fallback.SingleUserDetailsManagerImpl;
import software.netcore.radman.security.ldap.LdapProperties;
import software.netcore.radman.security.ldap.LocalLdapAuthoritiesPopulator;
import software.netcore.radman.security.local.LocalAuthenticationProvider;

/**
 * @since v. 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    private final SystemUserRepo systemUserRepo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        // Not using Spring CSRF here to be able to use plain HTML for the login page
		http.csrf().disable()
				// Register our CustomRequestCache, that saves unauthorized access attempts, so
				// the user is redirected after login.
				.requestCache()
                    .requestCache(new VaadinRequestCache())
				// Restrict access to our application.
				.and()
					.authorizeRequests()
				    // Allow all flow internal requests.
					.requestMatchers(VaadinRequestMatcher::matches)
						.permitAll()
				    // Allow all requests by logged in users.
					.anyRequest()
						.authenticated()
				// Configure the login page.
				.and()
					.formLogin()
					.loginPage(LOGIN_URL)
					.permitAll()
                    .successHandler(loginSuccessHandler())
					.failureUrl(LOGIN_FAILURE_URL)
				// Configure logout
				.and()
					.logout()
					.logoutSuccessUrl(LOGOUT_SUCCESS_URL);
        //@formatter:on
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // Vaadin Flow static resources //
                "/VAADIN/**",
                // the standard favicon URI
                "/favicon.ico",
                // the robots exclusion standard
                "/robots.txt",
                // web application manifest //
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                // (development mode) static resources //
                "/frontend/**",
                // (development mode) webjars //
                "/webjars/**",
                // (production mode) static resources //
                "/frontend-es5/**", "/frontend-es6/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(fallbackAuthenticationProvider());
        auth.authenticationProvider(localAuthenticationProvider());
        if (ldapProperties().isEnabled()) {
            //@formatter:off
            auth.ldapAuthentication()
                    .userSearchFilter(ldapProperties().getUserSearchFilter())
                    .userSearchBase(ldapProperties().getSearchBaseDn())
                    .contextSource()
                        .managerDn(ldapProperties().getManagerDn())
                        .managerPassword(ldapProperties().getManagerPassword())
                        .url(ldapProperties().getUrls())
                    .and()
                        .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator())
                        .rolePrefix("");
            //@formatter:on
        }
    }

    @Bean
    LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(systemUserRepo);
    }

    @Bean
    AuthenticationProvider localAuthenticationProvider() {
        return new LocalAuthenticationProvider(systemUserRepo, passwordEncoder());
    }

    @Bean
    AuthenticationProvider fallbackAuthenticationProvider() {
        return new FallbackAuthenticationProvider(fallbackUserDetailsManager());
    }

    @Bean
    SingleUserDetailsManager fallbackUserDetailsManager() {
        return new SingleUserDetailsManagerImpl();
    }

    @Bean
    LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
        return new LocalLdapAuthoritiesPopulator(systemUserRepo);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConfigurationProperties(prefix = "ldap")
    LdapProperties ldapProperties() {
        return new LdapProperties();
    }

}
