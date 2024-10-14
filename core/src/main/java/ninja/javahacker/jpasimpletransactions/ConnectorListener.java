package ninja.javahacker.jpasimpletransactions;

import lombok.NonNull;

/**
 * Callback that listen for events in the {@link Connector} class.
 * Mostly intended for logging purposes.
 * @author Victor Williams Stafusa da Silva
 */
public interface ConnectorListener {

    /**
     * Called when a connector is registered for some persistence unit.
     * @param persistenceUnit The name of the persistence unit which the connector was registered.
     * @param defaultConnector If the connector is the default connector.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void connectorRegistered(@NonNull String persistenceUnit, boolean defaultConnector) {
    }

    /**
     * Called when an operation starts within a connector.
     * @param persistenceUnit The name of the persistence unit which the operation started.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void operationStarted(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a transaction starts.
     * @param persistenceUnit The name of the persistence unit which the transaction started.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void startedTransaction(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a broken connection to the database was reestablished.
     * @param persistenceUnit The name of the persistence unit which the transaction started.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void renewedConnection(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a transaction finished with a commit.
     * @param persistenceUnit The name of the persistence unit that commited.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void finishedWithCommit(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a transaction finished with a rollback.
     * @param persistenceUnit The name of the persistence unit that rollback'd.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void finishedWithRollback(@NonNull String persistenceUnit) {
    }

    /**
     * Called when an operation finishes within a connector.
     * @param persistenceUnit The name of the persistence unit which the operation finished.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void operationFinished(@NonNull String persistenceUnit) {
    }

    /**
     * Called when a connector is unregistered for some persistence unit.
     * @param persistenceUnit The name of the persistence unit which the connector was registered.
     * @param defaultConnector If the connector was the default connector.
     * @throws IllegalArgumentException If {@code persistenceUnit} is {@code null}.
     */
    public default void connectorUnregistered(@NonNull String persistenceUnit, boolean defaultConnector) {
    }
}