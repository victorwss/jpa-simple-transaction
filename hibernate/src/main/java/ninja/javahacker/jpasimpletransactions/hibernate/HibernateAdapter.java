package ninja.javahacker.jpasimpletransactions.hibernate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import javax.persistence.EntityManager;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ExtendedEntityManager;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.hibernate.Session;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.jpa.HibernatePersistenceProvider;

/**
 * Implementation of {@link ProviderAdapter} for Hibernate.
 * @author Victor Williams Stafusa da Silva
 */
public final class HibernateAdapter implements ProviderAdapter {

    private static final HibernatePersistenceProvider PROVIDER = new HibernatePersistenceProvider();

    public static final HibernateAdapter CANONICAL = new HibernateAdapter();

    public HibernateAdapter() {
    }

    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return ExtendedEntityManager.unwrap(em) instanceof Session;
    }

    @Override
    @SuppressFBWarnings("FII_USE_FUNCTION_IDENTITY")
    public Connection getConnection(@NonNull EntityManager em) {
        return ((Session) ExtendedEntityManager.unwrap(ensureRecognition(em))).doReturningWork(c -> c);
    }

    @Override
    public HibernatePersistenceProvider getJpaProvider() {
        return PROVIDER;
    }

    @Override
    public boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return e instanceof JDBCConnectionException && "Unable to acquire JDBC Connection".equals(e.getMessage());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof HibernateAdapter;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}