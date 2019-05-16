package software.netcore.radman.buisness.service.auth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.netcore.radman.buisness.service.auth.AuthService;
import software.netcore.radman.data.internal.repo.RadCheckAttributeRepo;
import software.netcore.radman.data.internal.repo.RadReplyAttributeRepo;
import software.netcore.radman.data.radius.repo.RadCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupCheckRepo;
import software.netcore.radman.data.radius.repo.RadGroupReplyRepo;
import software.netcore.radman.data.radius.repo.RadReplyRepo;

/**
 * @since v. 1.0.0
 */
@Configuration
public class AuthServiceConfiguration {

    private final RadCheckRepo radCheckRepo;
    private final RadReplyRepo radReplyRepo;
    private final RadGroupCheckRepo radGroupCheckRepo;
    private final RadGroupReplyRepo radGroupReplyRepo;
    private final RadCheckAttributeRepo radCheckAttributeRepo;
    private final RadReplyAttributeRepo radReplyAttributeRepo;

    @Autowired
    public AuthServiceConfiguration(RadCheckRepo radCheckRepo,
                                    RadReplyRepo radReplyRepo,
                                    RadGroupCheckRepo radGroupCheckRepo,
                                    RadGroupReplyRepo radGroupReplyRepo,
                                    RadCheckAttributeRepo radCheckAttributeRepo,
                                    RadReplyAttributeRepo radReplyAttributeRepo) {
        this.radCheckRepo = radCheckRepo;
        this.radReplyRepo = radReplyRepo;
        this.radGroupCheckRepo = radGroupCheckRepo;
        this.radGroupReplyRepo = radGroupReplyRepo;
        this.radCheckAttributeRepo = radCheckAttributeRepo;
        this.radReplyAttributeRepo = radReplyAttributeRepo;
    }

    @Bean
    AuthService authService() {
        return new AuthService(radCheckRepo, radReplyRepo, radGroupCheckRepo,
                radGroupReplyRepo, radCheckAttributeRepo, radReplyAttributeRepo);
    }

}
