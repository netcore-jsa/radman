package software.netcore.radman.ui.validator;

import com.vaadin.flow.data.validator.RegexpValidator;

/**
 * @since v. 1.0.0
 */
public class PasswordValidator extends RegexpValidator {

    private static final String ERROR_MESSAGE = "Password requires one lowercase letter, uppercase letter and number. " +
            "Spaces, tabs nor unicode characters are not allowed.";

    private static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\!@#\\$%\\^&\\*\\(\\)\\-_\\=\\+\\.\\:;\\?]{8,}$";

    public PasswordValidator() {
        super(ERROR_MESSAGE, REGEX);
    }

}
