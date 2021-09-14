package ninja.javahacker.jpasimpletransactions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;

/**
 * Centralizes the creation and acquisition of {@link Connector} instances.
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class Database {
    private static final AtomicReference<Connector> DEFAULT_CONNECTOR = new AtomicReference<>();
    private static final Map<String, Connector> CONNECTOR_MAP = new ConcurrentHashMap<>(10);
    private static final Broadcaster MASTER_LISTENER = new Broadcaster();

    /**
     * Obtains the default connector.
     * @return The default connector.
     * @throws NoSuchElementException If there is no default connector registered yet.
     * @see #setDefaultConnector(Connector)
     */
    @NonNull
    public Connector getDefaultConnector() {
        return getDefaultConnectorIfDefined()
                .orElseThrow(() -> new NoSuchElementException("No registered default persistence unit."));
    }

    /**
     * Obtains an {@link Optional} containing the default connector if one was set, or an empty {@link Optional} if none was defined.
     * @return An {@link Optional} containing the default connector if one was set, or an empty {@link Optional} if none was defined.
     * @see #setDefaultConnector(Connector)
     */
    @NonNull
    @Synchronized
    public Optional<Connector> getDefaultConnectorIfDefined() {
        return Optional.ofNullable(DEFAULT_CONNECTOR.get());
    }

    /**
     * Obtains a connector through its persistence unit's name.
     * @param persistenceUnitName The connector's persistence unit name.
     * @return The connector for the given persistence unit name.
     * @throws NoSuchElementException If there is no connector registered for the given persistence unit name.
     * @throws IllegalArgumentException If {@code persistenceUnitName} is {@code null}.
     * @see #setDefaultConnector(Connector)
     * @see #setSecondaryConnector(Connector)
     * @see #addConnector(Connector, boolean)
     */
    @NonNull
    @Synchronized
    public Connector getConnector(@NonNull String persistenceUnitName) {
        var c = CONNECTOR_MAP.get(persistenceUnitName);
        if (c != null) return c;
        throw new NoSuchElementException("No registered persistence unit named " + persistenceUnitName + ".");
    }

    /**
     * Forgets the connector with the given persistence unit name, if it is registered.
     * @param persistenceUnitName The connector's persistence unit name.
     * @return An {@link Optional} containing the connector with the given persistence unit name or an empty one if none was found.
     * @throws IllegalArgumentException If {@code persistenceUnitName} is {@code null}.
     * @see #setDefaultConnector(Connector)
     * @see #setSecondaryConnector(Connector)
     * @see #addConnector(Connector, boolean)
     */
    @NonNull
    @Synchronized
    public Optional<Connector> removeConnector(@NonNull String persistenceUnitName) {
        var conn = CONNECTOR_MAP.remove(persistenceUnitName);
        if (conn == null) return Optional.empty();
        var defaultConnector = DEFAULT_CONNECTOR.get();
        var wasDefault = conn == defaultConnector;
        if (wasDefault) DEFAULT_CONNECTOR.set(null);
        MASTER_LISTENER.connectorUnregistered(persistenceUnitName, wasDefault);
        return Optional.of(conn);
    }

    /**
     * Forgets about all the registered connectors.
     */
    @NonNull
    @Synchronized
    public void removeAllConnectors() {
        var defaultConnector = DEFAULT_CONNECTOR.get();
        var defaultName = defaultConnector == null ? null : defaultConnector.getPersistenceUnitName();
        for (var c : CONNECTOR_MAP.keySet()) {
            MASTER_LISTENER.connectorUnregistered(c, c.equals(defaultName));
        }
        CONNECTOR_MAP.clear();
        DEFAULT_CONNECTOR.set(null);
    }

    /**
     * Adds a connector to the centralized pool of {@link Connector}s and set it as the default connector.
     * @param conn The {@link Connector} to add as the default connector.
     * @throws IllegalArgumentException If {@code conn} is {@code null}.
     * @throws IllegalStateException If a different connector with the same persistence unit name was already registered.
     */
    public void setDefaultConnector(@NonNull Connector conn) {
        addConnector(conn, true);
    }

    /**
     * Adds a connector to the centralized pool of {@link Connector}s but do not set it as the default connector.
     * @param conn The {@link Connector} to add.
     * @throws IllegalArgumentException If {@code conn} is {@code null}.
     * @throws IllegalStateException If a different connector with the same persistence unit name was already registered.
     */
    public void setSecondaryConnector(@NonNull Connector conn) {
        addConnector(conn, false);
    }

    /**
     * Adds a connector to the centralized pool of {@link Connector}s.
     * @param conn The {@link Connector} to add.
     * @param defaultConnector If the connector should be set as the default connector.
     * @throws IllegalArgumentException If {@code conn} is {@code null}.
     * @throws IllegalStateException If a different connector with the same persistence unit name was already registered.
     */
    @Synchronized
    public void addConnector(@NonNull Connector conn, boolean defaultConnector) {
        var pn = conn.getPersistenceUnitName();
        var c = CONNECTOR_MAP.get(pn);
        if (c != null && c != conn) throw new IllegalStateException("Connector was already registered.");
        CONNECTOR_MAP.put(pn, conn);
        if (defaultConnector) DEFAULT_CONNECTOR.set(conn);
        MASTER_LISTENER.connectorRegistered(pn, defaultConnector);
    }

    /**
     * Obtains an {@link ExtendedEntityManager} for the default persistence unit.
     * @return An {@link ExtendedEntityManager} for the default persistence unit.
     * @throws NoSuchElementException If there is no default connector registered yet.
     */
    public ExtendedEntityManager getEntityManager() {
        return getDefaultConnector().getEntityManager();
    }

    /**
     * Obtains an {@link ExtendedEntityManager} for a given persistence unit name.
     * @param persistenceUnitName The persistence unit's name.
     * @return An {@link ExtendedEntityManager} for a given persistence unit name.
     * @throws NoSuchElementException If there is no connector registered for the given persistence unit name.
     * @throws IllegalArgumentException If {@code persistenceUnitName} is {@code null}.
     */
    public ExtendedEntityManager getEntityManager(@NonNull String persistenceUnitName) {
        return getConnector(persistenceUnitName).getEntityManager();
    }

    /**
     * Register a {@link ConnectorListener} that will receive notification of database events.
     * @param listener The {@link ConnectorListener} that will receive notification of database events.
     */
    public void addListener(@NonNull ConnectorListener listener) {
        MASTER_LISTENER.add(listener);
    }

    /**
     * Unregister a {@link ConnectorListener}, so it won't receive notification of database events anymore.
     * @param listener The {@link ConnectorListener} that won't receive notification of database events anymore.
     */
    public void removeListener(@NonNull ConnectorListener listener) {
        MASTER_LISTENER.remove(listener);
    }

    /**
     * Unregister all the registered {@link ConnectorListener}s.
     */
    public void clearListeners() {
        MASTER_LISTENER.clear();
    }

    /**
     * Gets the {@link ConnectorListener} instance which is responsible to broadcast messages to all
     * registered {@link ConnectorListener}s.
     * @return The {@link ConnectorListener} instance which is responsible to broadcast messages to all
     *     registered {@link ConnectorListener}s.
     */
    @SuppressFBWarnings("MS_EXPOSE_REP")
    public Broadcaster getListener() {
        return MASTER_LISTENER;
    }
}
