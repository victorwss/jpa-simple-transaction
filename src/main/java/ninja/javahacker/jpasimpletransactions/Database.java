package ninja.javahacker.jpasimpletransactions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class Database {
    private static final AtomicReference<String> DEFAULT_CONNECTOR_NAME = new AtomicReference<>();
    private static final Map<String, Connector> CONNECTOR_MAP = new ConcurrentHashMap<>();

    private static final List<ConnectorListener> LISTENERS = new CopyOnWriteArrayList<>();

    private static final ConnectorListener MASTER_LISTENER = ConnectorListener.newBroadcaster(LISTENERS);

    @NonNull
    @Synchronized
    public Connector getDefaultConnector() {
        String name = DEFAULT_CONNECTOR_NAME.get();
        if (name == null) throw new IllegalStateException("No registered default persistence unit.");
        return getConnector(name);
    }

    @NonNull
    @Synchronized
    public Connector getConnector(@NonNull String persistenceUnitName) {
        Connector c = CONNECTOR_MAP.get(persistenceUnitName);
        if (c != null) return c;
        return addConnector(persistenceUnitName, persistenceUnitName.equals(DEFAULT_CONNECTOR_NAME.get()));
    }

    public Connector setDefaultConnector(@NonNull String persistenceUnitName) {
        return addConnector(persistenceUnitName, true);
    }

    public Connector setSecondaryConnector(@NonNull String persistenceUnitName) {
        return addConnector(persistenceUnitName, false);
    }

    @Synchronized
    public Connector addConnector(@NonNull String persistenceUnitName, boolean defaultConnector) {
        Connector c = CONNECTOR_MAP.computeIfAbsent(persistenceUnitName, n -> new Connector(persistenceUnitName));
        if (defaultConnector) DEFAULT_CONNECTOR_NAME.set(persistenceUnitName);
        return c;
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
