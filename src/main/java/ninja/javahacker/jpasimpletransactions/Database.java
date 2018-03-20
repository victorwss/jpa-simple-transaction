package ninja.javahacker.jpasimpletransactions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;

/**
 * @author Victor Williams Stafusa da Silva
 */
@UtilityClass
public class Database {
    private final AtomicReference<String> DEFAULT_CONNECTOR_NAME = new AtomicReference<>();
    private final Map<String, Connector> MAP = new ConcurrentHashMap<>();

    private final List<ConnectorListener> LISTENERS = new CopyOnWriteArrayList<>();

    private final ConnectorListener MASTER_LISTENER = ConnectorListener.newBroadcaster(LISTENERS);

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
        Connector c = MAP.get(persistenceUnitName);
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
        Connector c = MAP.computeIfAbsent(persistenceUnitName, n -> new Connector(persistenceUnitName));
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
