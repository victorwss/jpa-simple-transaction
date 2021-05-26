package ninja.javahacker.jpasimpletransactions;

import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * Callback that listen for events in the {@link Connector} class.
 * Mostly intended for logging purposes.
 * @author Victor Williams Stafusa da Silva
 */
public interface ConnectorListener {

    public default void connectorStarted(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a transaction starts.
     * @param persistenceUnit The name of the persistence unit which the transaction started.
     */
    public default void startedTransaction(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a broken connection to the database was restablished.
     * @param persistenceUnit The name of the persistence unit which the transaction started.
     */
    public default void renewedConnection(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a transaction finished with a commit.
     * @param persistenceUnit The name of the persistence unit that commited.
     */
    public default void finishedWithCommit(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a transaction finished with a rollback.
     * @param persistenceUnit The name of the persistence unit that rollback'd.
     */
    public default void finishedWithRollback(@NonNull String persistenceUnit) {
    }

    public default void connectorClosed(@NonNull String persistenceUnit) {
    }

    /**
     * Creates a listener that broadcast its call to other listeners.
     * @param list The listeners that will receive the broadcasted calls.
     * @return A new listener that broadcast its call to the listeners in the given list.
     * @throws IllegalArgumentException If {@code list} is {@code null}.
     */
    public static ConnectorListener newBroadcaster(@NonNull Iterable<ConnectorListener> list) {

        return new ConnectorListener() {
            private void broadcast(BiConsumer<ConnectorListener, String> cons, String persistenceUnit) {
                list.forEach(listener -> cons.accept(listener, persistenceUnit));
            }

            @Override
            public void connectorStarted(@NonNull String persistenceUnit) {
                broadcast(ConnectorListener::connectorStarted, persistenceUnit);
            }

            @Override
            public void startedTransaction(@NonNull String persistenceUnit) {
                broadcast(ConnectorListener::startedTransaction, persistenceUnit);
            }

            @Override
            public void renewedConnection(@NonNull String persistenceUnit) {
                broadcast(ConnectorListener::renewedConnection, persistenceUnit);
            }

            @Override
            public void finishedWithCommit(@NonNull String persistenceUnit) {
                broadcast(ConnectorListener::finishedWithCommit, persistenceUnit);
            }

            @Override
            public void finishedWithRollback(@NonNull String persistenceUnit) {
                broadcast(ConnectorListener::finishedWithRollback, persistenceUnit);
            }

            @Override
            public void connectorClosed(@NonNull String persistenceUnit) {
                broadcast(ConnectorListener::connectorClosed, persistenceUnit);
            }
        };
    }
}
