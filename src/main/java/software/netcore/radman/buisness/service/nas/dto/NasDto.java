package software.netcore.radman.buisness.service.nas.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @since v. 1.0.0
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class NasDto {

    private Integer id;

    @NotEmpty(message = "Name is required")
    @Length(min = 3, max = 128, message = "Name length has to be within 3 to 128 characters")
    private String nasName;

    @Length(max = 32, message = "Short name length can be maximally 32 characters")
    private String shortName;

    @Length(max = 30, message = "Type length can be maximally 30 characters")
    private String type;

    @Range(min = 1, max = 65535, message = "Port number has to be within range 1 to 65535")
    private Integer ports;

    @NotEmpty(message = "Secret is required")
    @Length(max = 60, message = "Secret length can be maximally 60 characters")
    private String secret;

    @Length(max = 64, message = "Server length can be maximally 64 characters")
    private String server;

    @Length(max = 50, message = "Community length can be maximally 50 characters")
    private String community;

    @Length(max = 200, message = "Description length can be maximally 200 characters")
    private String description;

    @Override
    public String toString() {
        return "NasDto{" +
                "id=" + id +
                ", nasName='" + nasName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", type='" + type + '\'' +
                ", ports=" + ports +
                ", secret='" + (secret == null ? null : (secret.length() + " characters")) + '\'' +
                ", server='" + server + '\'' +
                ", community='" + community + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
