package ninja.javahacker.jpasimpletransactions;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private static final List<ConnectorListener> LISTENERS = new CopyOnWriteArrayList<>();
    private static final ConnectorListener MASTER_LISTENER = ConnectorListener.newBroadcaster(LISTENERS);

    @NonNull
    @Synchronized
    public Connector getDefaultConnector() {
        var defaultConnector = DEFAULT_CONNECTOR.get();
        if (defaultConnector == null) throw new NoSuchElementException("No registered default persistence unit.");
        return defaultConnector;
    }

    @NonNull
    @Synchronized
    public Connector getConnector(@NonNull String persistenceUnitName) {
        var c = CONNECTOR_MAP.get(persistenceUnitName);
        if (c != null) return c;
        throw new NoSuchElementException("No registered persistence unit named " + persistenceUnitName + ".");
    }

    @NonNull
    @Synchronized
    public Connector removeConnector(@NonNull String persistenceUnitName) {
        return CONNECTOR_MAP.remove(persistenceUnitName);
    }

    @NonNull
    @Synchronized
    public void removeAllConnectors() {
        CONNECTOR_MAP.clear();
        DEFAULT_CONNECTOR.set(null);
    }

    public void setDefaultConnector(@NonNull Connector conn) {
        addConnector(conn, true);
    }

    public void setSecondaryConnector(@NonNull Connector conn) {
        addConnector(conn, false);
    }

    @Synchronized
    public void addConnector(@NonNull Connector conn, boolean defaultConnector) {
        var pn = conn.getPersistenceUnitName();
        var c = CONNECTOR_MAP.get(pn);
        if (c != null && c != conn) throw new IllegalArgumentException("Connector was already registered.");
        CONNECTOR_MAP.put(pn, conn);
        if (defaultConnector) DEFAULT_CONNECTOR.set(conn);
    }

    public ExtendedEntityManager getEntityManager() {
        return getDefaultConnector().getEntityManager();
    }

    public ExtendedEntityManager getEntityManager(@NonNull String persistenceUnitName) {
        return getConnector(persistenceUnitName).getEntityManager();
    }

    public void addListener(@NonNull ConnectorListener listener) {
        LISTENERS.add(listener);
    }

    public void removeListener(@NonNull ConnectorListener listener) {
        LISTENERS.remove(listener);
    }

    public ConnectorListener getListener() {
        return MASTER_LISTENER;
    }
}
