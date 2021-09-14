package ninja.javahacker.jpasimpletransactions.openjpa;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;
import org.apache.openjpa.persistence.PersistenceProviderImpl;

/**
 * Implementation of {@link ProviderAdapter} for Open JPA.
 * @see #CANONICAL
 * @author Victor Williams Stafusa da Silva
 */
public final class OpenJpaAdapter implements ProviderAdapter {

    private static final PersistenceProviderImpl PROVIDER = new PersistenceProviderImpl();

    /**
     * Precreated instance. Use this to avoid needlessly reinstantiating this class if you can.
     */
    public static final OpenJpaAdapter CANONICAL = new OpenJpaAdapter(42);

    /**
     * Do not use this directly. Prefer to use {@link #CANONICAL}.
     * @deprecated This constructor exists solely for being usable through the {@code ServiceLoader} mechanism which uses it.
     *     Otherwise, there should have no public constructors within this class.
     */
    @Deprecated
    public OpenJpaAdapter() {
    }

    private OpenJpaAdapter(int x) {
    }

    /**
     * {@inheritDoc}
     * @param emf {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean recognizes(@NonNull EntityManagerFactory emf) {
        return emf instanceof OpenJPAEntityManagerFactory;
    }

    /**
     * {@inheritDoc}
     * @param em {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return ExtendedEntityManager.unwrap(em) instanceof OpenJPAEntityManager;
    }

    /**
     * {@inheritDoc}
     * @param em {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Connection getConnection(@NonNull EntityManager em) {
        OpenJPAEntityManager oem = (OpenJPAEntityManager) ExtendedEntityManager.unwrap(ensureRecognition(em));
        return (Connection) oem.getConnection();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public PersistenceProvider getJpaProvider() {
        return PROVIDER;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public OpenJpaConnectorFactory config() {
        return new OpenJpaConnectorFactory();
    }

    /**
     * Returns 3.
     * <p>Since this class is stateless, all of its instances are considered equals, so they must have the same hash code,
     * which was arbitrarily choosen to be 3.</p>
     * @return 3.
     */
    @Override
    public int hashCode() {
        return 3;
    }

    /**
     * Returns {@code true} if the {@code other} object is the same as of this class or {@code false} otherwise.
     * <p>Since this class is stateless, all of its instances are considered equals.</p>
     * @param other Some other object to determine if equals to {@code this} one.
     * @return {@code true} if the {@code other} object is the same as of this class or {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof OpenJpaAdapter;
    }

    /**
     * Simply returns the name of this class.
     * @return The name of this class.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}