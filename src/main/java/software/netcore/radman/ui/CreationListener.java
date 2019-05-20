package software.netcore.radman.ui;

/**
 * @param <T>
 * @since v. 1.0.0
 */
@FunctionalInterface
public interface CreationListener<T> {

    void onCreated(Object source, T bean);

}
