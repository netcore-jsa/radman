package software.netcore.radman.data.conf;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@Validated
@NoArgsConstructor
public class JpaProperties {

    private String showSql;

    @Valid
    private Hibernate hibernate = new Hibernate();

    @Getter
    @Setter
    @NoArgsConstructor
    @SuppressWarnings("WeakerAccess")
    public static class Hibernate {

        private String ddlAuto;

        private String dialect;

    }

}
