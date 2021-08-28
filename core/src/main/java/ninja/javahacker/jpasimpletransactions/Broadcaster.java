package ninja.javahacker.jpasimpletransactions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Synchronized;

/**
 * An implementation of a {@link ConnectorListener} that broadcasts its call to others {@link ConnectorListener}.
 * @author Victor Williams Stafusa da Silva
 */
public final class Broadcaster implements ConnectorListener {

    @NonNull
    private final List<ConnectorListener> list;

    @FunctionalInterface
    private static interface Register {
        public void accept(ConnectorListener listener, String persistenceUnit, boolean defaultConnector);
    }

    /**
     * Sole constructor.
     */
    public Broadcaster() {
        this.list = new CopyOnWriteArrayList<>();
    }

    private void broadcast(BiConsumer<ConnectorListener, String> cons, String persistenceUnit) {
        list.forEach(listener -> cons.accept(listener, persistenceUnit));
    }

    private void broadcast(Register cons, String persistenceUnit, boolean defaultConnector) {
        list.forEach(listener -> cons.accept(listener, persistenceUnit, defaultConnector));
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     * @param defaultConnector {@inheritDoc}
     */
    @Override
    public void connectorRegistered(@NonNull String persistenceUnit, boolean defaultConnector) {
        broadcast(ConnectorListener::connectorRegistered, persistenceUnit, defaultConnector);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     */
    @Override
    public void operationStarted(@NonNull String persistenceUnit) {
        broadcast(ConnectorListener::operationStarted, persistenceUnit);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     */
    @Override
    public void startedTransaction(@NonNull String persistenceUnit) {
        broadcast(ConnectorListener::startedTransaction, persistenceUnit);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     */
    @Override
    public void renewedConnection(@NonNull String persistenceUnit) {
        broadcast(ConnectorListener::renewedConnection, persistenceUnit);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     */
    @Override
    public void finishedWithCommit(@NonNull String persistenceUnit) {
        broadcast(ConnectorListener::finishedWithCommit, persistenceUnit);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     */
    @Override
    public void finishedWithRollback(@NonNull String persistenceUnit) {
        broadcast(ConnectorListener::finishedWithRollback, persistenceUnit);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     */
    @Override
    public void operationFinished(@NonNull String persistenceUnit) {
        broadcast(ConnectorListener::operationFinished, persistenceUnit);
    }

    /**
     * {@inheritDoc}
     * @param persistenceUnit {@inheritDoc}
     * @param defaultConnector {@inheritDoc}
     */
    @Override
    public void connectorUnregistered(@NonNull String persistenceUnit, boolean defaultConnector) {
        broadcast(ConnectorListener::connectorUnregistered, persistenceUnit, defaultConnector);
    }

    /**
     * Register a {@link ConnectorListener} that will receive notification of database events.
     * @param listener The {@link ConnectorListener} that will receive notification of database events.
     */
    @Synchronized
    public void add(@NonNull ConnectorListener listener) {
        list.add(listener);
    }

    /**
     * Unregister a {@link ConnectorListener}, so it won't receive notification of database events anymore.
     * @param listener The {@link ConnectorListener} that won't receive notification of database events anymore.
     */
    @Synchronized
    public void remove(@NonNull ConnectorListener listener) {
        list.remove(listener);
    }

    /**
     * Unregister all the registered {@link ConnectorListener}s.
     */
    @Synchronized
    public void clear() {
        list.clear();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Tells if this object is equals to another object. However, each broadcaster is unique, so it is equals
     * only to itself.
     * @param other Another object for comparing equality.
     * @return {@code other == this}.
     */
    @Override
    public boolean equals(Object other) {
        return other == this;
    }

    /**
     * Gives the {@link String} representation of this object.
     * @return The {@link String} representation of this object.
     */
    @Override
    public String toString() {
        return "Broadcaster-" + hashCode();
    }
}
