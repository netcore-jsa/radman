package software.netcore.radman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @since v. 1.0.0
 */
@SpringBootApplication
public class Radman extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Radman.class, args);
    }

}
