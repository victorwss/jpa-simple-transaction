package ninja.javahacker.jpasimpletransactions;

import java.sql.Connection;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;
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

    public default EntityManager ensureRecognition(@NonNull EntityManager em) {
        if (recognizes(em)) return em;
        var a = em.getClass().getName();
        var b = getClass().getName();
        throw new IllegalArgumentException("That EntityManager (" + a + ") is not recognized by this ProviderAdapter (" + b + ").");
    }

    public Connection getConnection(@NonNull EntityManager em);

    public PersistenceProvider getJpaProvider();

    public default boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return false;
    }

    public static ProviderAdapter findFor(@NonNull EntityManager em) {
        for (ProviderAdapter impl : ServiceLoader.load(ProviderAdapter.class)) {
            if (impl.recognizes(em)) return impl;
        }
        var a = em.getClass().getName();
        throw new UnsupportedOperationException("That EntityManager (" + a + ") is not recognized by any know ProviderAdapter.");
    }

    public static Stream<ProviderAdapter> all() {
        return ServiceLoader.load(ProviderAdapter.class).stream().map(Provider::get);
    }
}
