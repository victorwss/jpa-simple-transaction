package ninja.javahacker.jpasimpletransactions.hibernate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import javax.persistence.EntityManager;
import lombok.NonNull;
import ninja.javahacker.jpasimpletransactions.ProviderAdapter;
import org.hibernate.Session;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.jpa.HibernatePersistenceProvider;

/**
 * Implementation of {@link ProviderAdapter} for Hibernate.
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class HibernateAdapter implements ProviderAdapter {

    public static final HibernateAdapter INSTANCE = new HibernateAdapter();

    private final HibernatePersistenceProvider provider;

    private HibernateAdapter() {
        this.provider = new HibernatePersistenceProvider();
    }

    @Override
    public boolean recognizes(@NonNull EntityManager em) {
        return em instanceof Session;
    }

    @Override
    @SuppressFBWarnings("FII_USE_FUNCTION_IDENTITY")
    public Connection getConnection(@NonNull EntityManager em) {
        if (!recognizes(em)) throw new UnsupportedOperationException();
        return ((Session) em).doReturningWork(c -> c);
    }

    @Override
    public HibernatePersistenceProvider getJpaProvider() {
        return provider;
    }

    @Override
    public boolean shouldTryToReconnect(@NonNull RuntimeException e) {
        return e instanceof JDBCConnectionException && "Unable to acquire JDBC Connection".equals(e.getMessage());
    }
}