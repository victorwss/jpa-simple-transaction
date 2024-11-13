package ninja.javahacker.jpasimpletransactions.config;

import jakarta.persistence.spi.PersistenceProvider;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
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

    /**
     * An {@link URL} representing a place that is not interesting nor usable for anything. Namely {@code "http://0.0.0.0/"}.
     */
    public static final URL NOWHERE = ((Supplier<URL>) () -> {
        try {
            return new URI("http://0.0.0.0/").toURL();
        } catch (URISyntaxException | MalformedURLException x) {
            throw new AssertionError(x);
        }
    }).get();

    /**
     * Gets the set of explicitly declared entity classes that should be recognized as entity types.
     * @return The set of explicitly declared entity classes that should be recognized as entity types.
     */
    public Set<Class<?>> getEntities();

    /**
     * Set the entity classes that should be recognized as entity types.
     * @param entities The entity classes to define.
     * @return A new instance of this class which is similar to {@code this}, but with the given entity classes.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withEntities(@NonNull Set<Class<?>> entities) throws IllegalArgumentException;

    /**
     * Set the entity classes that should be recognized as entity types.
     * @param entities The entity classes to define.
     * @return A new instance of this class which is similar to {@code this}, but with the given entity classes.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public default E withEntities(@NonNull Class<?>... entities) throws IllegalArgumentException {
        return withEntities(Set.of(entities));
    }

    /**
     * Add an entity class to the set of recognized entity types.
     * @param entityClass The entity class to add.
     * @return A new instance of this class which is similar to {@code this}, but with the given entity class added.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public default E addEntity(@NonNull Class<?> entityClass) throws IllegalArgumentException {
        var m = new ArrayList<>(getEntities());
        m.add(entityClass);
        return withEntities(Set.copyOf(m));
    }

    /**
     * Removes an entity class to the set of recognized entity types.
     * @param entityClass The entity class to remove.
     * @return A new instance of this class which is similar to {@code this}, but with the given entity class removed.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public default E removeEntity(@NonNull Class<?> entityClass) throws IllegalArgumentException {
        var m = new ArrayList<>(getEntities());
        m.remove(entityClass);
        return withEntities(Set.copyOf(m));
    }

    /**
     * Removes all the declared entity classes from the set of recognized entity types.
     * @return A new instance of this class which is similar to {@code this}, but without any explicitly declared entity class.
     */
    public default E clearEntities() {
        return withEntities(Set.of());
    }

    /**
     * Returns the object representing the persistence provider to which this instance will eventually connect.
     * @return The object representing the persistence provider to which this instance will eventually connect.
     */
    public ProviderAdapter getProviderAdapter();

    /**
     * Returns an {@code Optional} containing the {@link URL} for the JAR file or directory that is the root of the persistence unit.
     * If there is no such {@link URL} or if this was not implemented for the provider implementation, it will either return an empty
     * {@link Optional} or {@link #NOWHERE}, depending on what works considering the internal details of the persistence provider.
     * @implSpec If the implementer does not override this method, it will always return an empty {@link Optional}.
     * @return The {@link URL} for the JAR file or directory that is the root of the persistence unit.
     */
    public default Optional<URL> getPersistenceUnitUrl() {
        return Optional.empty();
    }

    /**
     * Gets the explicitly declared scoped annotation.
     * @return The explicitly declared scoped annotation.
     */
    public Class<? extends Annotation> getScopedAnnotation();

    /**
     * Gets the explicitly declared scoped annotation.
     * @param scopedAnnotation The explicitly declared scoped annotation.
     * @return A new instance of this class which is similar to {@code this}, but with the explicitly declared scoped annotation.
     * @throws IllegalArgumentException If the parameter is {@code null}.
     */
    public E withScopedAnnotation(@NonNull Class<? extends Annotation> scopedAnnotation) throws IllegalArgumentException;

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws WrongProviderException {@inheritDoc}
     */
    @Override
    public default Connector connect() throws WrongProviderException {
        String pu = getPersistenceUnitName();
        Map<String, String> properties = getProperties();
        ProviderAdapter pa = getProviderAdapter();
        PersistenceProvider pp = pa.getJpaProvider();
        var spui = new SimplePersistenceUnitInfo(
                getPersistenceUnitUrl(),
                pp.getClass(),
                pu,
                getEntities(),
                getScopedAnnotation(),
                properties
        );
        var emf = pp.createContainerEntityManagerFactory(spui, properties);
        if (emf == null) throw new WrongProviderException();
        return Connector.create(pu, emf, pa);
    }
}