package software.netcore.radman.buisness.exception;

/**
 * @since v. 1.0.0
 */
public class ValidationException extends Exception {

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

}
