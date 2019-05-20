package software.netcore.radman.ui;

/**
 * @since v. 1.0.0
 */
@FunctionalInterface
public interface UpdateListener<T> {

    void onUpdated(Object source, T bean);

}
