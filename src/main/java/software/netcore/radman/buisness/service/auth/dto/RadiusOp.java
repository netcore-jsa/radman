package software.netcore.radman.buisness.service.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @see <a href='https://wiki.freeradius.org/config/Operators'>FreeRadius operators documentation</a>
 * @since v. 1.0.0
 */
@Getter
@RequiredArgsConstructor
public enum RadiusOp {

    OP1("="),
    OP2(":="),
    OP3("=="),
    OP4("+="),
    OP5("!=");

    private final String value;

}
