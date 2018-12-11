package ninja.javahacker.jpasimpletransactions;

import java.net.URL;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import lombok.NonNull;

/**
 * Service Provider Interface used to get access to vendor-specific features of JPA Providers.
 *
 * <p>Implementations of this class are expected to be instantiated by the means of the {@link ServiceLoader}
 * mechanism. Instances can be obtained via the {@link #findFor(EntityManager)} static method.</p>
 *
 * @author Victor Williams Stafusa da Silva
 */
public interface ProviderAdapter {
    public boolean recognizes(@NonNull EntityManager em);

    public Connection getConnection(@NonNull EntityManager em);

    public PersistenceProvider getJpaProvider();

    public default Optional<URL> getUrl() {
        return Optional.empty();
    }

    public default boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return false;
    }

    /*public default EntityManagerFactory createEntityManagerFactory(
            @NonNull String unit,
            @NonNull Map<String, String> properties)
    {
        return getJpaProvider().createEntityManagerFactory(unit, properties);
    }*/

    public default EntityManagerFactory createContainerEntityManagerFactory(
            @NonNull PersistenceUnitInfo unit,
            @NonNull Map<String, String> properties)
    {
        return getJpaProvider().createContainerEntityManagerFactory(unit, properties);
    }

    public static ProviderAdapter findFor(@NonNull EntityManager em) {
        for (ProviderAdapter impl : ServiceLoader.load(ProviderAdapter.class)) {
            if (impl.recognizes(em)) return impl;
        }
        throw new UnsupportedOperationException();
    }
}
