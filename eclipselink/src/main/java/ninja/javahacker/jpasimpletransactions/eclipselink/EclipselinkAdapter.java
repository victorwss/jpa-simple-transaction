package ninja.javahacker.jpasimpletransactions.eclipselink;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.spi.PersistenceProvider;
import java.sql.Connection;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;

/**
 * Implementation of {@link ProviderAdapter} for Eclipselink.
 * @see #CANONICAL
 * @author Victor Williams Stafusa da Silva
 */
public final class EclipselinkAdapter implements ProviderAdapter {

    private static final PersistenceProvider PROVIDER = new org.eclipse.persistence.jpa.PersistenceProvider();

    /**
     * Precreated instance. Use this to avoid needlessly re-instantiating this class if you can.
     */
    public static final EclipselinkAdapter CANONICAL = new EclipselinkAdapter(42);

    /**
     * Do not use this directly. Prefer to use {@link #CANONICAL}.
     * @deprecated This constructor exists solely for being usable through the {@code ServiceLoader} mechanism which uses it.
     *     Otherwise, there should have no public constructors within this class.
     */
    @Deprecated
    public EclipselinkAdapter() {
    }

    private EclipselinkAdapter(int x) {
    }

    /**
     * {@inheritDoc}
     * @param emf {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean recognizes(@NonNull EntityManagerFactory emf) {
        return emf instanceof JpaEntityManagerFactory;
    }

    /**
     * {@inheritDoc}
     * @param em {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return ExtendedEntityManager.unwrap(em) instanceof JpaEntityManager;
    }

    /**
     * {@inheritDoc}
     * @param em {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Connection getConnection(@NonNull EntityManager em) {
        EntityTransaction et = ensureRecognition(em).getTransaction();
        boolean needTransaction = !et.isActive();
        try {
            if (needTransaction) em.getTransaction().begin();
            return em.unwrap(Connection.class);
        } finally {
            if (needTransaction) em.getTransaction().commit();
        }
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
    @Override
    public EclipselinkConnectorFactory config() {
        return new EclipselinkConnectorFactory();
    }

    /**
     * Returns 2.
     * <p>Since this class is stateless, all of its instances are considered equals, so they must have the same hash code,
     * which was arbitrarily choosen to be 2.</p>
     * @return 2.
     */
    @Override
    public int hashCode() {
        return 2;
    }

    /**
     * Returns {@code true} if the {@code other} object is the same as of this class or {@code false} otherwise.
     * <p>Since this class is stateless, all of its instances are considered equals.</p>
     * @param other Some other object to determine if equals to {@code this} one.
     * @return {@code true} if the {@code other} object is the same as of this class or {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof EclipselinkAdapter;
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