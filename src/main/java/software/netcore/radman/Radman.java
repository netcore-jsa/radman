package software.netcore.radman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @see <a href="https://vaadin.com/forum/thread/17784869/vaadin-14-with-spring-security-login-page-not-loading">
 * why ErrorMvcAutoConfiguration.class is excluded</a>
 * @since v. 1.0.0
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class Radman extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Radman.class, args);
    }

}
