package ninja.javahacker.jpasimpletransactions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceProvider;
import java.sql.Connection;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;

/**
 * Service Provider Interface used to get access to vendor-specific features of JPA Providers.
 *
 * <p>Implementations of this class are expected to be instantiated by the means of the {@link ServiceLoader}
 * mechanism. Instances can be obtained via the {@link #findFor(EntityManager)} static method.</p>
 *
 * @author Victor Williams Stafusa da Silva
 */
public interface ProviderAdapter {

    /**
     * Tests if the persistence provider represented by {@code this} recognizes an {@link EntityManager}
     * as one of theirs {@link EntityManager}.
     * @param em The {@link EntityManager} to be tested.
     * @return {@code true} if the {@link EntityManager} is recognized, {@code false} otherwise.
     * @throws IllegalArgumentException If {@code em} is {@code null}.
     */
    public default boolean recognizes(@NonNull EntityManager em) {
        return recognizes(em.getEntityManagerFactory());
    }

    /**
     * Tests if the persistence provider represented by {@code this} recognizes an {@link EntityManagerFactory}
     * as one of theirs {@link EntityManagerFactory}.
     * @param emf The {@link EntityManagerFactory} to be tested.
     * @return {@code true} if the {@link EntityManagerFactory} is recognized, {@code false} otherwise.
     * @throws IllegalArgumentException If {@code emf} is {@code null}.
     */
    public boolean recognizes(@NonNull EntityManagerFactory emf);

    /**
     * Tests if the persistence provider represented by {@code this} recognizes an {@link EntityManager}
     * as one of theirs {@link EntityManager} and return it if it indeed does.
     * @param em The {@link EntityManager} to be tested.
     * @return The given {@code em} parameter object.
     * @throws IllegalArgumentException If {@code em} is {@code null} or is not recognized.
     */
    public default EntityManager ensureRecognition(@NonNull EntityManager em) {
        if (recognizes(em)) return em;
        var a = em.getClass().getName();
        var b = getClass().getName();
        throw new IllegalArgumentException("That EntityManager (" + a + ") is not recognized by this ProviderAdapter (" + b + ").");
    }

    /**
     * Tests if the persistence provider represented by {@code this} recognizes an {@link EntityManagerFactory}
     * as one of theirs {@link EntityManagerFactory} and return it if it indeed does.
     * @param emf The {@link EntityManagerFactory} to be tested.
     * @return The given {@code emf} parameter object.
     * @throws IllegalArgumentException If {@code emf} is {@code null} or is not recognized.
     */
    public default EntityManagerFactory ensureRecognition(@NonNull EntityManagerFactory emf) {
        if (recognizes(emf)) return emf;
        var a = emf.getClass().getName();
        var b = getClass().getName();
        throw new IllegalArgumentException("That EntityManagerFactory (" + a + ") is not recognized by this ProviderAdapter (" + b + ").");
    }

    /**
     * Obtains the underlying {@link Connection} used by an {@link EntityManager}.
     * This makes this method useful to use JDBC directly inside JPA transactions.
     * @param em The {@link EntityManager} to acquire the underlying {@link Connection}.
     * @return The underlying {@link Connection} used by the given {@link EntityManager}.
     * @throws IllegalArgumentException If {@code em} is {@code null}.
     */
    public Connection getConnection(@NonNull EntityManager em);

    /**
     * Obtains the underlying {@link PersistenceProvider} wrapped by this {@code ProviderAdapter}.
     * @return The underlying {@link PersistenceProvider} wrapped by this {@code ProviderAdapter}.
     */
    public PersistenceProvider getJpaProvider();

    /**
     * Determines if a reconnection should be automatically tried if the underlying connection is lost in the case of the given
     * exception happening.
     * @param e Some exception that could be handled by automatically reconnecting to the database.
     * @return {@code true} if a an automatic reconnection could possibly handle the exception, {@code false} if this is impossible
     *     or unlikely.
     * @throws IllegalArgumentException If {@code e} is {@code null}.
     */
    public default boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return false;
    }

    /**
     * Finds a suitable {@link ProviderAdapter} for the given {@link EntityManagerFactory}.
     * @implSpec The list of knows {@link ProviderAdapter} is reloaded in every call to this method.
     * @param emf The given {@link EntityManagerFactory}.
     * @return The {@link ProviderAdapter} found.
     * @throws UnsupportedOperationException No known {@link ProviderAdapter} recognized the given {@link EntityManagerFactory}.
     * @throws IllegalArgumentException If {@code emf} is {@code null}.
     */
    public static ProviderAdapter findFor(@NonNull EntityManagerFactory emf) {
        for (ProviderAdapter impl : ServiceLoader.load(ProviderAdapter.class)) {
            if (impl.recognizes(emf)) return impl;
        }
        var a = emf.getClass().getName();
        throw new UnsupportedOperationException("That EntityManagerFactory (" + a + ") is not recognized by any know ProviderAdapter.");
    }

    /**
     * Finds a suitable {@link ProviderAdapter} for the given {@link EntityManager}.
     * @implSpec The list of knows {@link ProviderAdapter} is reloaded in every call to this method.
     * @param em The given {@link EntityManager}.
     * @return The {@link ProviderAdapter} found.
     * @throws UnsupportedOperationException No known {@link ProviderAdapter} recognized the given {@link EntityManager}.
     * @throws IllegalArgumentException If {@code em} is {@code null}.
     */
    public static ProviderAdapter findFor(@NonNull EntityManager em) {
        for (ProviderAdapter impl : ServiceLoader.load(ProviderAdapter.class)) {
            if (impl.recognizes(em)) return impl;
        }
        var a = em.getClass().getName();
        throw new UnsupportedOperationException("That EntityManager (" + a + ") is not recognized by any know ProviderAdapter.");
    }

    /**
     * Streams all of the known {@link ProviderAdapter}s.
     * @return All the {@link ProviderAdapter}s found, wrapped inside {@code Supplier}s of {@code Maybe}s
     *     because some of them might fail to load.
     * @implSpec The list of known {@link ProviderAdapter}s is reloaded in every call to this method.
     */
    public static Stream<Supplier<Maybe<ProviderAdapter>>> all() {
        return ServiceLoader.load(ProviderAdapter.class).stream().map(Maybe::wrap);
    }

    /**
     * Produces an object for configuring a persistence unit in order to create {@link Connector}s.
     * @return An object for configuring a persistence unit in order to create {@link Connector}s.
     */
    public ProviderConnectorFactory<?> config();
}
