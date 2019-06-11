package software.netcore.radman.buisness.exception;

/**
 * @since v. 1.0.0
 */
public class DuplicityException extends Exception {

    public DuplicityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicityException(String message) {
        super(message);
    }

}
