package ninja.javahacker.jpasimpletransactions;

import java.util.function.BiConsumer;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ConnectorListener {
    public default void connectorStarted(@NonNull Connector c) {
    }

    public default void startedTransaction(@NonNull Connector c) {
    }

    public default void renewedConnection(@NonNull Connector c) {
    }

    public default void finishedWithCommit(@NonNull Connector c) {
    }

    public default void finishedWithRollback(@NonNull Connector c) {
    }

    public default void connectorClosed(@NonNull Connector c) {
    }

    public static ConnectorListener newBroadcaster(Iterable<ConnectorListener> list) {

        return new ConnectorListener() {
            private void broadcast(BiConsumer<ConnectorListener, Connector> cons, Connector c) {
                list.forEach(listener -> cons.accept(listener, c));
            }

            @Override
            public void connectorStarted(@NonNull Connector c) {
                broadcast(ConnectorListener::connectorStarted, c);
            }

            @Override
            public void startedTransaction(@NonNull Connector c) {
                broadcast(ConnectorListener::startedTransaction, c);
            }

            @Override
            public void renewedConnection(@NonNull Connector c) {
                broadcast(ConnectorListener::renewedConnection, c);
            }

            @Override
            public void finishedWithCommit(@NonNull Connector c) {
                broadcast(ConnectorListener::finishedWithCommit, c);
            }

            @Override
            public void finishedWithRollback(@NonNull Connector c) {
                broadcast(ConnectorListener::finishedWithRollback, c);
            }

            @Override
            public void connectorClosed(@NonNull Connector c) {
                broadcast(ConnectorListener::connectorClosed, c);
            }
        };
    }
}
