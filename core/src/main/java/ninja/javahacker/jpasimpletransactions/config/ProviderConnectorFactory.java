package ninja.javahacker.jpasimpletransactions.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.spi.PersistenceProvider;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;

/**
 * Used as a building block acting as a delegate to wrap
 * {@link DefaultPersistenceProperties} instances into composed properties
 * classes for specific JPA vendors.
 *
 * @param <E> The properties class for the specific JPA vendor.
 * @author Victor Williams Stafusa da Silva
 */
public interface ProviderConnectorFactory<E extends ProviderConnectorFactory<E>> extends StandardConnectorFactory<E> {

    public List<Class<?>> getEntities();

    public E withEntities(@NonNull List<Class<?>> entities);

    public default E addEntity(@NonNull Class<?> entityClass) {
        var m = new ArrayList<>(getEntities());
        m.add(entityClass);
        return withEntities(List.copyOf(m));
    }

    public default E removeEntity(@NonNull Class<?> entityClass) {
        var m = new ArrayList<>(getEntities());
        m.remove(entityClass);
        return withEntities(List.copyOf(m));
    }

    public default E clearEntities() {
        return withEntities(List.of());
    }

    public ProviderAdapter getProviderAdapter();

    public default Optional<URL> getPersistenceUnitUrl() {
        return Optional.empty();
    }

    @Override
    public default Connector connect() {
        String pu = getPersistenceUnitName();
        Map<String, String> properties = getProperties();
        ProviderAdapter pa = getProviderAdapter();
        PersistenceProvider pp = pa.getJpaProvider();
        var spui = new SimplePersistenceUnitInfo(getPersistenceUnitUrl(), pp.getClass(), pu, getEntities(), properties);
        var emf = pp.createContainerEntityManagerFactory(spui, properties);
        return new Connector(pu, emf);
    }
}