package software.netcore.radman.ui.validator;

import com.vaadin.flow.data.validator.RegexpValidator;

/**
 * @since v. 1.0.0
 */
public class UsernameValidator extends RegexpValidator {

    private static final String ERROR_MESSAGE = "Username can consist of letters, " +
            "numbers, any of the \"._-@\" characters, and has to have at least 3 characters.";

    private static final String REGEX = "^[a-zA-Z0-9._\\-@]{3,}$";

    public UsernameValidator() {
        super(ERROR_MESSAGE, REGEX);
    }

}
