package ninja.javahacker.jpasimpletransactions.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.spi.PersistenceProvider;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;

/**
 * A collection of properties used to instantiate a {@link Connector}.
 * The provider will be a specific JPA vendor as determined by implementations of this interface.
 *
 * @param <E> The properties class for the specific JPA vendor.
 * @author Victor Williams Stafusa da Silva
 */
public interface ProviderConnectorFactory<E extends ProviderConnectorFactory<E>> extends StandardConnectorFactory<E> {

    public Set<Class<?>> getEntities();

    public E withEntities(@NonNull Set<Class<?>> entities);

    public default E addEntity(@NonNull Class<?> entityClass) {
        var m = new ArrayList<>(getEntities());
        m.add(entityClass);
        return withEntities(Set.copyOf(m));
    }

    public default E removeEntity(@NonNull Class<?> entityClass) {
        var m = new ArrayList<>(getEntities());
        m.remove(entityClass);
        return withEntities(Set.copyOf(m));
    }

    public default E clearEntities() {
        return withEntities(Set.of());
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
        return Connector.create(pu, emf, pa);
    }
}