package ninja.javahacker.jpasimpletransactions;

import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * Callback that listen for events in the {@link Connector} class.
 * Mostly intended for loggin purposes.
 * @author Victor Williams Stafusa da Silva
 */
public interface ConnectorListener {
    public default void connectorStarted(@NonNull String persistenceUnit) {
    }

    public default void startedTransaction(@NonNull String persistenceUnit) {
    }

    public default void renewedConnection(@NonNull String persistenceUnit) {
    }

    public default void finishedWithCommit(@NonNull String persistenceUnit) {
    }

    public default void finishedWithRollback(@NonNull String persistenceUnit) {
    }

    public default void connectorClosed(@NonNull String persistenceUnit) {
    }

    public static ConnectorListener newBroadcaster(Iterable<ConnectorListener> list) {

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
