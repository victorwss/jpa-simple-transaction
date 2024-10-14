package ninja.javahacker.jpasimpletransactions.hibernate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceProvider;
import java.sql.Connection;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.jpa.HibernatePersistenceProvider;

/**
 * Implementation of {@link ProviderAdapter} for Hibernate.
 * @see #CANONICAL
 * @author Victor Williams Stafusa da Silva
 */
public final class HibernateAdapter implements ProviderAdapter {

    private static final HibernatePersistenceProvider PROVIDER = new HibernatePersistenceProvider();

    /**
     * Precreated instance. Use this to avoid needlessly re-instantiating this class if you can.
     */
    public static final HibernateAdapter CANONICAL = new HibernateAdapter(42);

    /**
     * Do not use this directly. Prefer to use {@link #CANONICAL}.
     * @deprecated This constructor exists solely for being usable through the {@code ServiceLoader} mechanism which uses it.
     *     Otherwise, there should have no public constructors within this class.
     */
    @Deprecated
    public HibernateAdapter() {
    }

    private HibernateAdapter(int x) {
    }

    /**
     * {@inheritDoc}
     * @param emf {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean recognizes(@NonNull EntityManagerFactory emf) {
        return emf instanceof SessionFactory;
    }

    /**
     * {@inheritDoc}
     * @param em {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return ExtendedEntityManager.unwrap(em) instanceof Session;
    }

    /**
     * {@inheritDoc}
     * @param em {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @SuppressFBWarnings("FII_USE_FUNCTION_IDENTITY")
    public Connection getConnection(@NonNull EntityManager em) {
        return ((Session) ExtendedEntityManager.unwrap(ensureRecognition(em))).doReturningWork(c -> c);
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
     * @param e {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return e instanceof JDBCConnectionException && "Unable to acquire JDBC Connection".equals(e.getMessage());
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public HibernateConnectorFactory config() {
        return new HibernateConnectorFactory();
    }

    /**
     * Returns 1.
     * <p>Since this class is stateless, all of its instances are considered equals, so they must have the same hash code,
     * which was arbitrarily chosen to be 1.</p>
     * @return 1.
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /**
     * Returns {@code true} if the {@code other} object is the same as of this class or {@code false} otherwise.
     * <p>Since this class is stateless, all of its instances are considered equals.</p>
     * @param other Some other object to determine if equals to {@code this} one.
     * @return {@code true} if the {@code other} object is the same as of this class or {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof HibernateAdapter;
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